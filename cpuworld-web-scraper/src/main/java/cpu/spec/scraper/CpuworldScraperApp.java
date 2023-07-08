package cpu.spec.scraper;

import cpu.spec.scraper.exception.DirectoryNotFoundException;
import cpu.spec.scraper.exception.ElementNotFoundException;
import cpu.spec.scraper.factory.LoggerFactory;
import cpu.spec.scraper.file.CpuSpecificationWriter;
import cpu.spec.scraper.parser.CpuOverviewParser;
import cpu.spec.scraper.parser.CpuSeriesParser;
import cpu.spec.scraper.parser.CpuSpecificationParser;
import cpu.spec.scraper.utils.FileUtils;
import cpu.spec.scraper.utils.LogUtils;
import cpu.spec.scraper.utils.TimeUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class CpuworldScraperApp {
    private static final Logger LOGGER = LoggerFactory.getLogger();
    private static final String HOST_URL = "https://www.cpu-world.com";

    public static void main(String[] args) throws DirectoryNotFoundException, IOException {
        LOGGER.info("Starting Cpu World Scraper.");
        String outputDir = FileUtils.getOutputDirectoryPath("dataset");
        String outputFile = "cpuworld-cpus.csv";

        List<CpuSpecificationModel> specifications = extractSelectedCpuSpecifications();

        CpuSpecificationWriter.writeCsvFile(specifications, outputDir + outputFile);
        LOGGER.info("Finished Cpu World Scraper. Output at: " + outputDir + outputFile);
    }


    private static List<CpuSpecificationModel> extractSelectedCpuSpecifications() {
        List<String> specificationLinks = List.of("https://www.cpu-world.com/CPUs/Xeon/Intel-Xeon%208272CL.html");
        LOGGER.info("Given " + specificationLinks.size() + " Specification Links.");

        List<CpuSpecificationModel> specifications = extractSpecifications(specificationLinks);
        LOGGER.info("Extracted " + specifications.size() + " CPU Specifications.");
        return specifications;
    }

    private static List<CpuSpecificationModel> extractAllCpuSpecifications() throws ElementNotFoundException, IOException {
        List<String> overviewLinks = CpuOverviewParser.extractNavigationLinks();
        LOGGER.info("Extracted " + overviewLinks.size() + " Overview Links.");

        List<String> seriesLinks = extractNavigationLinks(overviewLinks);
        LOGGER.info("Extracted " + seriesLinks.size() + " Series Links.");

        List<String> familyLinks = extractNavigationLinks(seriesLinks);
        LOGGER.info("Extracted " + familyLinks.size() + " Family Links.");

        List<String> specificationLinks = extractNavigationLinks(familyLinks);
        LOGGER.info("Extracted " + specificationLinks.size() + " Specification Links.");

        List<CpuSpecificationModel> specifications = extractSpecifications(specificationLinks.stream().map(href -> HOST_URL + href).toList());
        LOGGER.info("Extracted " + specifications.size() + " CPU Specifications.");
        return specifications;
    }

    private static List<String> extractNavigationLinks(List<String> inputLinks) {
        List<String> outputLinks = new ArrayList<>();
        for (String link : inputLinks) {
            String fullLink = HOST_URL + link;
            try {
                outputLinks.addAll(CpuSeriesParser.extractNavigationLinks(fullLink));
            } catch (Exception e) {
                LOGGER.warning(LogUtils.exceptionMessage(e, fullLink));
            }
        }
        return outputLinks;
    }

    private static List<CpuSpecificationModel> extractSpecifications(List<String> specificationLinks) {
        List<CpuSpecificationModel> specifications = new ArrayList<>();
        for (String link : specificationLinks) {
            try {
                specifications.add(CpuSpecificationParser.extractSpecification(link));
                if (specifications.size() % 250 == 0) {
                    LOGGER.info(LogUtils.progressMessage(specifications, specificationLinks, "CPU Specifications"));
                }
            } catch (Exception e) {
                LOGGER.warning(LogUtils.exceptionMessage(e, link));
            }
        }
        TimeUtils.sleepBetween(10000, 3000);
        return specifications;
    }
}
