package cpu.spec.scraper.file;

import cpu.spec.scraper.ScraperConfig;
import cpu.spec.scraper.factory.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * Protects existing datasets from being overwritten by incomplete scraper runs (e.g. blocked requests).
 */
public abstract class DatasetOutput {
    /**
     * minimum share of the existing row count a new run must reach to replace the dataset
     */
    public static final double MIN_ROW_RATIO = 0.8;
    private static final Logger LOGGER = LoggerFactory.getLogger();

    /**
     * @param outputDir directory of the dataset (e.g. "../dataset/")
     * @param fileName  dataset file name (e.g. "amd-cpus.csv")
     * @param newRows   number of extracted records
     * @return the dataset path, or "*.incomplete.csv" if the new run has less than {@link #MIN_ROW_RATIO} of the
     * existing rows (unless forced with -Dscraper.force=true or a smoke test limit is active)
     * @throws IOException if the existing dataset cannot be read
     */
    public static Path resolve(String outputDir, String fileName, int newRows) throws IOException {
        Path target = Path.of(outputDir, fileName);
        if (ScraperConfig.isLimited()) {
            Path smokeTest = Path.of(outputDir, fileName.replace(".csv", ".limited.csv"));
            LOGGER.warning("Smoke test limit active: writing to " + smokeTest + " instead of the dataset.");
            return smokeTest;
        }
        long existingRows = countRows(target);
        if (newRows < existingRows * MIN_ROW_RATIO && !ScraperConfig.isForced()) {
            Path incomplete = Path.of(outputDir, fileName.replace(".csv", ".incomplete.csv"));
            LOGGER.warning(String.format("Only %d records extracted, the existing dataset has %d. Keeping %s and writing to %s "
                    + "(use -Dscraper.force=true to overwrite anyway).", newRows, existingRows, target, incomplete));
            return incomplete;
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
