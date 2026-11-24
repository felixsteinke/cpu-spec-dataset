package cpu.spec.dataset.api.dataset;

import cpu.spec.dataset.api.file.ResourceReader;
import cpu.spec.dataset.api.mapping.CsvColumnIndexMapping;
import cpu.spec.dataset.api.mapping.CsvColumnModification;
import cpu.spec.dataset.api.mapping.CsvColumnNameMapping;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Stream;

public abstract class Dataset {
    /**
     * regex to split csv lines by ',' while ignoring ',' within quotes
     */
    public static final String CSV_SPLIT_REGEX = ",(?=([^\"]*\"[^\"]*\")*[^\"]*$)";
    private static final Logger LOGGER = Logger.getLogger(Dataset.class.getName());
    private static final String BOM = "﻿";
    private final String DATASET_PATH;

    /**
     * @param datasetResourcePath resource file path
     */
    protected Dataset(String datasetResourcePath) {
        DATASET_PATH = datasetResourcePath;
    }

    /**
     * @return name of the dataset extracted of the path
     */
    public String getName() {
        String[] pathSplit = DATASET_PATH.split("/");
        if (pathSplit.length > 0) {
            return pathSplit[pathSplit.length - 1];
        } else {
            return "Unknown Dataset";
        }
    }

    /**
     * @return column mapping by header names with fallbacks for cpu data (readonly)
     */
    public abstract CsvColumnNameMapping getColumnNameMapping();

    /**
     * @return modifications for cpu data (readonly)
     */
    public abstract CsvColumnModification getColumnModifications();

    /**
     * Resolves the header names of {@link #getColumnNameMapping()} to column indices of the dataset.
     * Header names that do not exist in the dataset are skipped with a warning.
     *
     * @return column index mapping with fallbacks for cpu data
     * @throws IOException if no resource at dataset path can be found
     */
    public CsvColumnIndexMapping getColumnMapping() throws IOException {
        Map<String, Integer> indexByHeader = getNormalizedHeaderIndices();
        CsvColumnNameMapping names = getColumnNameMapping();
        CsvColumnIndexMapping mapping = new CsvColumnIndexMapping();
        mapping.name = resolveIndices(names.name, indexByHeader);
        mapping.productCollection = resolveIndices(names.productCollection, indexByHeader);
        mapping.cores = resolveIndices(names.cores, indexByHeader);
        mapping.threads = resolveIndices(names.threads, indexByHeader);
        mapping.baseFrequency = resolveIndices(names.baseFrequency, indexByHeader);
        mapping.maxFrequency = resolveIndices(names.maxFrequency, indexByHeader);
        mapping.tdp = resolveIndices(names.tdp, indexByHeader);
        mapping.launchDate = resolveIndices(names.launchDate, indexByHeader);
        mapping.sourceUrl = resolveIndices(names.sourceUrl, indexByHeader);
        return mapping;
    }

    /**
     * @return configured header names of {@link #getColumnNameMapping()} that do not exist in the dataset
     * @throws IOException if no resource at dataset path can be found
     */
    public List<String> getUnresolvedColumnNames() throws IOException {
        Map<String, Integer> indexByHeader = getNormalizedHeaderIndices();
        CsvColumnNameMapping names = getColumnNameMapping();
        List<String> unresolved = new ArrayList<>();
        for (String[] options : List.of(names.name, names.productCollection, names.cores, names.threads,
                names.baseFrequency, names.maxFrequency, names.tdp, names.launchDate, names.sourceUrl)) {
            if (options == null) continue;
            for (String header : options) {
                if (!indexByHeader.containsKey(normalizeHeader(header))) {
                    unresolved.add(header);
                }
            }
        }
        return unresolved;
    }

    /**
     * @return raw csv lines without header
     * @throws IOException if no resource at dataset path can be found
     */
    public Stream<String> getDatasetLines() throws IOException {
        return ResourceReader.getResourceReader(DATASET_PATH).lines().skip(1);
    }

    /**
     * @return map of (columnIndex, csvHeader) with surrounding quotes removed
     * @throws IOException            if no resource at dataset path can be found
     * @throws NoSuchElementException if csv lines are empty
     */
    public Map<Integer, String> getDatasetColumns() throws IOException, NoSuchElementException {
        String headerLine;
        try (BufferedReader reader = ResourceReader.getResourceReader(DATASET_PATH)) {
            headerLine = reader.lines().findFirst().orElseThrow();
        }
        if (headerLine.startsWith(BOM)) {
            headerLine = headerLine.substring(BOM.length());
        }
        Map<Integer, String> map = new HashMap<>();
        String[] split = headerLine.split(CSV_SPLIT_REGEX, -1);
        for (int i = 0; i < split.length; i++) {
            map.put(i, unquote(split[i].trim()));
        }
        return map;
    }

    /**
     * @param header raw header name (example: "Intel® Turbo Boost Technology 2.0 Frequency‡")
     * @return lowercase alphanumeric representation (example: intelturboboosttechnology20frequency)
     */
    static String normalizeHeader(String header) {
        return header.toLowerCase(Locale.ROOT).replaceAll("[^\\p{L}\\p{N}]", "");
    }

    private Map<String, Integer> getNormalizedHeaderIndices() throws IOException {
        Map<String, Integer> indexByHeader = new HashMap<>();
        getDatasetColumns().entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(e -> indexByHeader.putIfAbsent(normalizeHeader(e.getValue()), e.getKey()));
        return indexByHeader;
    }

    private int[] resolveIndices(String[] headers, Map<String, Integer> indexByHeader) {
        if (headers == null) {
            return new int[]{};
        }
        return Arrays.stream(headers)
                .map(header -> {
                    Integer index = indexByHeader.get(normalizeHeader(header));
                    if (index == null) {
                        LOGGER.warning(getName() + ": column '" + header + "' not found in dataset header.");
                    }
                    return index;
                })
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .toArray();
    }

    private static String unquote(String value) {
        if (value.length() >= 2 && value.startsWith("\"") && value.endsWith("\"")) {
            return value.substring(1, value.length() - 1);
        }
        return value;
    }
}
