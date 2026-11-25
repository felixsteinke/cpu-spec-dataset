package cpu.spec.scraper.file;

import cpu.spec.scraper.ScraperConfig;
import cpu.spec.scraper.factory.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * Writes the records of a scraper run into its dataset:
 * <ul>
 *     <li>default: the records are merged into the dataset, CPUs the run did not find keep their row (see {@link DatasetMerge})</li>
 *     <li>{@code -Dscraper.force=true}: the records replace the dataset</li>
 *     <li>{@code -Dscraper.limit} (smoke test): the records are written to *.limited.csv, the dataset is unchanged</li>
 * </ul>
 */
public abstract class DatasetOutput {
    /**
     * share of the dataset rows a run should update, otherwise the merge is logged as warning (e.g. blocked requests)
     */
    public static final double MIN_ROW_RATIO = 0.8;
    private static final Logger LOGGER = LoggerFactory.getLogger();

    /**
     * Writes the records of a scraper run in its own csv format, e.g. {@code file -> CsvWriter.write(file, header, rows)}.
     */
    @FunctionalInterface
    public interface RecordWriter {
        void write(Path file) throws IOException;
    }

    /**
     * @param outputDir directory of the dataset (e.g. "../dataset/")
     * @param fileName  dataset file name (e.g. "amd-cpus.csv")
     * @param writer    writes the records of the run to the given file
     * @return the dataset path, or "*.limited.csv" for a smoke test
     * @throws IOException           if a file cannot be read or written
     * @throws IllegalStateException if the run has no records (the dataset is unchanged)
     */
    public static Path write(String outputDir, String fileName, RecordWriter writer) throws IOException {
        Path target = Path.of(outputDir, fileName);
        if (ScraperConfig.isLimited()) {
            Path smokeTest = Path.of(outputDir, fileName.replace(".csv", ".limited.csv"));
            LOGGER.warning("Smoke test limit active: writing to " + smokeTest + " instead of the dataset.");
            writer.write(smokeTest);
            return smokeTest;
        }
        Path records = Files.createTempFile(fileName.replace(".csv", "-"), ".csv");
        try {
            writer.write(records);
            if (countRows(records) == 0) {
                throw new IllegalStateException("No records extracted, " + target + " is unchanged.");
            }
            long previousRows = countRows(target);
            if (ScraperConfig.isForced()) {
                DatasetMerge.Result result = DatasetMerge.replace(records, target);
                LOGGER.info("Replaced " + target + " (scraper.force, " + previousRows + " rows before): " + result);
            } else {
                DatasetMerge.Result result = DatasetMerge.merge(records, target);
                String message = "Merged " + (result.updated() + result.added()) + " records into " + target + ": " + result;
                if (result.updated() < previousRows * MIN_ROW_RATIO) {
                    LOGGER.warning(String.format("%s. Only %d of %d CPUs of the dataset were found again (blocked requests or "
                            + "removed from the website), the others are kept (use -Dscraper.force=true to replace the dataset).",
                            message, result.updated(), previousRows));
                } else {
                    LOGGER.info(message);
                }
            }
        } finally {
            Files.deleteIfExists(records);
        }
        return target;
    }

    static long countRows(Path file) throws IOException {
        if (!Files.exists(file)) {
            return 0;
        }
        try (Stream<String> lines = Files.lines(file)) {
            return Math.max(0, lines.filter(line -> !line.isBlank()).count() - 1);
        }
    }
}
