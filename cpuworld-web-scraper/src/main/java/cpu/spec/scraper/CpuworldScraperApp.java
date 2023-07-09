package cpu.spec.scraper;

import cpu.spec.scraper.exception.DirectoryNotFoundException;
import cpu.spec.scraper.factory.LoggerFactory;
import cpu.spec.scraper.file.CpuSpecificationWriter;
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

        List<CpuSpecificationModel> specifications = extractXeonPlatinumAndXeonGoldSpecifications();

        CpuSpecificationWriter.writeCsvFile(specifications, outputDir + outputFile);
        LOGGER.info("Finished Cpu World Scraper. Output at: " + outputDir + outputFile);
    }

    private static List<CpuSpecificationModel> extractXeonPlatinumAndXeonGoldSpecifications() {
        List<String> familyLinks = List.of(
                "https://www.cpu-world.com/CPUs/Xeon/TYPE-Xeon Platinum.html",
                "https://www.cpu-world.com/CPUs/Xeon/TYPE-Xeon Gold.html");
        LOGGER.info("Given " + familyLinks.size() + " Family Links.");

        List<String> specificationHrefs = extractNavigationLinks(familyLinks);
        List<String> specificationLinks = specificationHrefs.stream().map(href -> HOST_URL + href).toList();
        LOGGER.info("Extracted " + specificationLinks.size() + " Specification Links.");

        List<CpuSpecificationModel> specifications = extractSpecifications(specificationLinks);
        LOGGER.info("Extracted " + specifications.size() + " of " + specificationLinks.size() + " CPU Specifications.");
        return specifications;
    }

    private static List<CpuSpecificationModel> extractSelectedCpuSpecifications() {
        List<String> specificationLinks = List.of(
                "https://www.cpu-world.com/CPUs/Xeon/Intel-Xeon 8272CL.html",
                "https://www.cpu-world.com/CPUs/Xeon/Intel-Xeon 8370C.html",
                "https://www.cpu-world.com/CPUs/Xeon/Intel-Xeon 8373C.html",
                "https://www.cpu-world.com/CPUs/Xeon/Intel-Xeon 6268CL.html");
        LOGGER.info("Given " + specificationLinks.size() + " Specification Links.");

        List<CpuSpecificationModel> specifications = extractSpecifications(specificationLinks);
        LOGGER.info("Extracted " + specifications.size() + " CPU Specifications.");
        return specifications;
    }

    private static List<String> extractNavigationLinks(List<String> inputLinks) {
        List<String> outputLinks = new ArrayList<>();
        for (String link : inputLinks) {
            try {
                outputLinks.addAll(CpuSeriesParser.extractNavigationLinks(link));
            } catch (Exception e) {
                LOGGER.warning(LogUtils.exceptionMessage(e, link));
            }
        }
        return outputLinks;
    }

    private static List<CpuSpecificationModel> extractSpecifications(List<String> specificationLinks) {
        List<CpuSpecificationModel> specifications = new ArrayList<>();
        for (String link : specificationLinks) {
            try {
                TimeUtils.sleepBetween(10000, 3000);
                specifications.add(CpuSpecificationParser.extractSpecification(link));
                if (specifications.size() % 250 == 0) {
                    LOGGER.info(LogUtils.progressMessage(specifications, specificationLinks, "CPU Specifications"));
                }

            } catch (Exception e) {
                LOGGER.warning(LogUtils.exceptionMessage(e, link));
                LOGGER.info("Retrying extraction of: " + link);
                try {
                    specifications.add(CpuSpecificationParser.extractSpecification(link));
                    if (specifications.size() % 250 == 0) {
                        LOGGER.info(LogUtils.progressMessage(specifications, specificationLinks, "CPU Specifications"));
                    }
                } catch (Exception ex) {
                    LOGGER.severe(LogUtils.exceptionMessage(e, link));
                }
            }
        }
        return specifications;
    }
}
