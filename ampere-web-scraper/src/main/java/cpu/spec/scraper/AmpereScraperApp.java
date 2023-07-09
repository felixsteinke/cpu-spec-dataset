package cpu.spec.scraper;

import cpu.spec.scraper.factory.LoggerFactory;
import cpu.spec.scraper.file.CpuSpecificationWriter;
import cpu.spec.scraper.parser.AltraProductParser;
import cpu.spec.scraper.utils.FileUtils;

import java.util.List;
import java.util.logging.Logger;

public class AmpereScraperApp {
    private static final Logger LOGGER = LoggerFactory.getLogger();

    public static void main(String[] args) throws Exception {
        LOGGER.info("Starting Ampere Scraper.");
        String outputDir = FileUtils.getOutputDirectoryPath("dataset");
        String outputFile = "ampere-cpus.csv";

        List<CpuSpecificationModel> specifications = AltraProductParser.extractSpecification();
        LOGGER.info("Extracted " + specifications.size() + " CPU Specifications.");

        CpuSpecificationWriter.writeCsvFile(specifications, outputDir + outputFile);
        LOGGER.info("Finished Ampere Scraper. Output at: " + outputDir + outputFile);
    }
}
