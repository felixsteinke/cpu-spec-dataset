package cpu.spec.scraper;

import cpu.spec.scraper.exception.DirectoryNotFoundException;
import cpu.spec.scraper.exception.ElementNotFoundException;
import cpu.spec.scraper.factory.LoggerFactory;
import cpu.spec.scraper.file.CpuSpecificationWriter;
import cpu.spec.scraper.parser.CpuProductParser;
import cpu.spec.scraper.parser.CpuSeriesParser;
import cpu.spec.scraper.parser.CpuSpecificationParser;
import cpu.spec.scraper.utils.FileUtils;
import cpu.spec.scraper.utils.LogUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class IntelScraperApp {
    private static final Logger LOGGER = LoggerFactory.getLogger();
    private static final String HOST_URL = "https://ark.intel.com";

    public static void main(String[] args) throws ElementNotFoundException, IOException, DirectoryNotFoundException {
        LOGGER.info("Starting Intel Scraper.");
        String outputDir = FileUtils.getOutputDirectoryPath("dataset");
        String outputFile = "intel-cpus.csv";

        List<String> seriesLinks = CpuProductParser.extractSeriesLinks();
        LOGGER.info("Extracted " + seriesLinks.size() + " CPU Series Links.");

        List<String> specificationLinks = extractSpecificationLinks(seriesLinks);
        LOGGER.info("Extracted " + specificationLinks.size() + " CPU Specification Links.");

        List<CpuSpecificationModel> specifications = extractSpecifications(specificationLinks);
        LOGGER.info("Extracted " + specifications.size() + " CPU Specifications.");

        CpuSpecificationWriter.writeCsvFile(specifications, outputDir + outputFile);
        LOGGER.info("Finished Intel Scraper. Output at: " + outputDir + outputFile);
    }

    private static List<String> extractSpecificationLinks(List<String> seriesLinks) {
        List<String> specificationLinks = new ArrayList<>();
        for (String link : seriesLinks) {
            String fullLink = HOST_URL + link;
            try {
                specificationLinks.addAll(CpuSeriesParser.extractSpecificationLinks(fullLink));
            } catch (Exception e) {
                LOGGER.warning(LogUtils.exceptionMessage(e, fullLink));
            }
        }
        return specificationLinks;
    }

    private static List<CpuSpecificationModel> extractSpecifications(List<String> specificationLinks) {
        List<CpuSpecificationModel> specifications = new ArrayList<>();
        for (String link : specificationLinks) {
            String fullLink = HOST_URL + link;
            try {
                specifications.add(CpuSpecificationParser.extractSpecification(fullLink));
                if (specifications.size() % 250 == 0) {
                    LOGGER.info(LogUtils.progressMessage(specifications, specificationLinks, "CPU Specifications"));
                }
            } catch (Exception e) {
                LOGGER.warning(LogUtils.exceptionMessage(e, fullLink));
            }
        }
        return specifications;
    }
}
