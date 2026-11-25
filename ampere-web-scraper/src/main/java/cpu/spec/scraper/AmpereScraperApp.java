package cpu.spec.scraper;

import cpu.spec.scraper.factory.LoggerFactory;
import cpu.spec.scraper.file.CsvWriter;
import cpu.spec.scraper.file.DatasetOutput;
import cpu.spec.scraper.parser.ProductBriefParser;
import cpu.spec.scraper.utils.FileUtils;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class AmpereScraperApp {
    private static final Logger LOGGER = LoggerFactory.getLogger();
    /**
     * product family -> product brief with SKU table
     */
    private static final List<Map.Entry<String, String>> PRODUCT_BRIEFS = List.of(
            Map.entry("Ampere Altra", "https://amperecomputing.com/briefs/ampere-altra-family-product-brief"),
            Map.entry("AmpereOne", "https://amperecomputing.com/briefs/ampereone-family-product-brief"),
            Map.entry("AmpereOne M", "https://amperecomputing.com/briefs/ampereone-m-product-brief"));
    // columns of the previous dataset version first (stable dataset format)
    private static final List<String> HEADER = List.of("CpuName", "ClockSpeed", "Cores", "TDP", "SourceUrl", "Family");

    public static void main(String[] args) throws Exception {
        LOGGER.info("Starting Ampere Scraper.");
        String outputDir = FileUtils.getOutputDirectoryPath("dataset");
        String outputFile = "ampere-cpus.csv";

        List<CpuSpecificationModel> specifications = new ArrayList<>();
        for (Map.Entry<String, String> brief : PRODUCT_BRIEFS) {
            List<CpuSpecificationModel> extracted = ProductBriefParser.extractSpecifications(brief.getKey(), brief.getValue());
            LOGGER.info("Extracted " + extracted.size() + " " + brief.getKey() + " CPU Specifications.");
            specifications.addAll(extracted);
        }

        List<List<String>> rows = specifications.stream()
                .map(s -> List.of(s.cpuName(), s.clockSpeed(), s.cores(), s.tdp(), s.sourceUrl(), s.family()))
                .toList();
        Path output = DatasetOutput.resolve(outputDir, outputFile, rows.size());
        CsvWriter.write(output, HEADER, rows);
        LOGGER.info("Finished Ampere Scraper with " + rows.size() + " CPU Specifications. Output at: " + output);
    }
}
