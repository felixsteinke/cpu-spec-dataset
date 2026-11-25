package cpu.spec.scraper;

import cpu.spec.scraper.factory.LoggerFactory;
import cpu.spec.scraper.file.CsvReader;
import cpu.spec.scraper.file.CsvWriter;
import cpu.spec.scraper.file.DatasetOutput;
import cpu.spec.scraper.parser.SpecificationTableParser;
import cpu.spec.scraper.parser.SpecificationTableParser.Table;
import cpu.spec.scraper.utils.FileUtils;

import java.nio.file.Path;
import java.util.*;
import java.util.logging.Logger;

public class AmdScraperApp {
    private static final Logger LOGGER = LoggerFactory.getLogger();
    private static final List<String> SPECIFICATION_PAGES = List.of(
            "https://www.amd.com/en/products/specifications/processors.html",
            "https://www.amd.com/en/products/specifications/server-processor.html");

    public static void main(String[] args) throws Exception {
        LOGGER.info("Starting AMD Scraper.");
        String outputDir = FileUtils.getOutputDirectoryPath("dataset");
        String outputFile = "amd-cpus.csv";

        // columns of the previous dataset version keep their order (stable dataset format), new columns are appended
        Set<String> header = new LinkedHashSet<>(CsvReader.readHeader(Path.of(outputDir, outputFile)));
        Map<String, Map<String, String>> processors = new LinkedHashMap<>();
        for (String url : SPECIFICATION_PAGES) {
            Table table = SpecificationTableParser.extractTable(url);
            LOGGER.info("Extracted " + table.rows().size() + " CPU Specifications from " + url);
            header.addAll(table.header());
            for (Map<String, String> row : table.rows()) {
                processors.putIfAbsent(row.getOrDefault("Name", ""), row);
            }
        }
        // keep the source url as last column
        header.remove(SpecificationTableParser.SOURCE_URL_COLUMN);
        header.add(SpecificationTableParser.SOURCE_URL_COLUMN);

        List<String> columns = new ArrayList<>(header);
        List<List<String>> rows = processors.values().stream()
                .map(row -> columns.stream().map(column -> row.getOrDefault(column, "")).toList())
                .toList();
        Path output = DatasetOutput.resolve(outputDir, outputFile, rows.size());
        CsvWriter.write(output, columns, rows);
        LOGGER.info("Finished AMD Scraper with " + rows.size() + " CPU Specifications. Output at: " + output);
    }
}
