package cpu.spec.scraper.file;

import cpu.spec.scraper.factory.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Merges the records of a scraper run into a dataset, so the CPU names (ids of the Dataset API) of the dataset are kept:
 * <ul>
 *     <li>records of CPUs in the dataset replace their row (updated)</li>
 *     <li>records of new CPUs are appended (added)</li>
 *     <li>CPUs the run did not find (e.g. removed from the website or blocked requests) keep their row (kept)</li>
 * </ul>
 * Columns of the dataset keep their order, new columns are appended. A CPU is identified by its name column
 * ({@code CpuName}, or {@code Name} for AMD) and additionally by {@code CpuId} if both files have it, because
 * different CPUs can have the same name (e.g. Intel).
 * <p>
 * Manual merge (see scripts/merge-dataset.sh): {@code java cpu.spec.scraper.file.DatasetMerge <records.csv> <dataset.csv>}
 */
public abstract class DatasetMerge {
    private static final Logger LOGGER = LoggerFactory.getLogger();
    private static final List<String> NAME_COLUMNS = List.of("CpuName", "Name");
    private static final String ID_COLUMN = "CpuId";

    /**
     * @param updated rows of the dataset replaced by a record
     * @param added   records of CPUs that were not in the dataset
     * @param kept    rows of the dataset without a record
     */
    public record Result(int updated, int added, int kept) {
        public int rows() {
            return updated + added + kept;
        }

        @Override
        public String toString() {
            return String.format("%d rows (%d updated, %d added, %d kept)", rows(), updated, added, kept);
        }
    }

    /**
     * @param records csv file with the records of a scraper run
     * @param dataset csv file of the dataset (may not exist yet), rewritten with the merged rows
     * @return number of updated, added and kept rows
     * @throws IOException              if a file cannot be read or written
     * @throws IllegalArgumentException if the files have no common name column
     */
    public static Result merge(Path records, Path dataset) throws IOException {
        return merge(CsvReader.readHeader(dataset), CsvReader.read(dataset),
                CsvReader.readHeader(records), CsvReader.read(records), dataset);
    }

    /**
     * Writes the records without merging (e.g. to replace a dataset), in the same csv format as a merge.
     *
     * @return number of written rows (all added)
     */
    public static Result replace(Path records, Path dataset) throws IOException {
        return merge(List.of(), List.of(), CsvReader.readHeader(records), CsvReader.read(records), dataset);
    }

    static Result merge(List<String> datasetHeader, List<Map<String, String>> datasetRows,
                        List<String> recordsHeader, List<Map<String, String>> records, Path output) throws IOException {
        List<String> header = new ArrayList<>(datasetHeader);
        recordsHeader.stream().filter(column -> !header.contains(column)).forEach(header::add);
        List<String> keyColumns = datasetRows.isEmpty() ? List.of() : keyColumns(datasetHeader, recordsHeader);

        Map<List<String>, Map<String, String>> recordsByKey = new LinkedHashMap<>();
        List<Map<String, String>> unmatched = new ArrayList<>();
        for (Map<String, String> record : records) {
            if (keyColumns.isEmpty() || recordsByKey.putIfAbsent(key(record, keyColumns), record) != null) {
                unmatched.add(record); // nothing to match against or a duplicate key: appended as it is
            }
        }

        List<List<String>> rows = new ArrayList<>();
        Set<List<String>> used = new HashSet<>();
        int updated = 0;
        int kept = 0;
        for (Map<String, String> row : datasetRows) {
            List<String> key = key(row, keyColumns);
            Map<String, String> record = recordsByKey.get(key);
            if (record != null && used.add(key)) {
                rows.add(values(record, header));
                updated++;
            } else {
                rows.add(values(row, header));
                kept++;
            }
        }
        int added = 0;
        for (Map.Entry<List<String>, Map<String, String>> entry : recordsByKey.entrySet()) {
            if (!used.contains(entry.getKey())) {
                rows.add(values(entry.getValue(), header));
                added++;
            }
        }
        for (Map<String, String> record : unmatched) {
            rows.add(values(record, header));
            added++;
        }
        CsvWriter.write(output, header, rows);
        return new Result(updated, added, kept);
    }

    /**
     * @return name column and, if both files have it, the id column
     * @throws IllegalArgumentException if the files have no common name column
     */
    static List<String> keyColumns(List<String> datasetHeader, List<String> recordsHeader) {
        String name = NAME_COLUMNS.stream()
                .filter(column -> datasetHeader.contains(column) && recordsHeader.contains(column))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Dataset and records need the same name column, one of " + NAME_COLUMNS));
        if (datasetHeader.contains(ID_COLUMN) && recordsHeader.contains(ID_COLUMN)) {
            return List.of(name, ID_COLUMN);
        }
        return List.of(name);
    }

    private static List<String> key(Map<String, String> row, List<String> keyColumns) {
        return keyColumns.stream().map(column -> row.getOrDefault(column, "")).toList();
    }

    private static List<String> values(Map<String, String> row, List<String> header) {
        return header.stream().map(column -> row.getOrDefault(column, "")).toList();
    }

    /**
     * Merges a records file into a dataset, e.g. the records of an incomplete run of an older version.
     *
     * @param args records csv file, dataset csv file
     */
    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.err.println("Usage: java cpu.spec.scraper.file.DatasetMerge <records.csv> <dataset.csv>");
            System.exit(1);
        }
        Path records = Path.of(args[0]);
        Path dataset = Path.of(args[1]);
        if (!Files.exists(records) || !Files.exists(dataset)) {
            System.err.println("File not found: " + (Files.exists(records) ? dataset : records));
            System.exit(1);
        }
        Result result = merge(records, dataset);
        LOGGER.info("Merged " + (result.updated() + result.added()) + " records of " + records + " into " + dataset + ": " + result);
    }
}
