package cpu.spec.scraper;

import cpu.spec.scraper.exception.ElementNotFoundException;
import cpu.spec.scraper.file.CpuSpecificationWriter;
import cpu.spec.scraper.parser.CpuProductScraper;
import cpu.spec.scraper.parser.CpuSeriesParser;
import cpu.spec.scraper.parser.CpuSpecificationParser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ScraperApp {
    public static void main(String[] args) throws ElementNotFoundException, IOException {
        String execDir = System.getProperty("user.dir");
        String outputDir = "./dataset/";
        String outputFile = "intel-cpus.csv";

        if (!new File(outputDir).exists()) {
            outputDir = "../dataset/";
            if (!new File(outputDir).exists()) {
                System.out.println("[WARNING] Execution directory: " + execDir);
                throw new ElementNotFoundException("Target Directory", outputDir);
            }
        }

        System.out.println("[INFO] Starting Intel Web Scraper.");

        List<String> seriesLinks = CpuProductScraper.extractSeriesLinks("https://ark.intel.com/content/www/us/en/ark.html#@Processors");
        System.out.println("[INFO] Extracted " + seriesLinks.size() + " CPU Series Links.");

        List<String> specificationLinks = extractSpecificationLinks(seriesLinks);
        System.out.println("[INFO] Extracted " + specificationLinks.size() + " CPU Specification Links.");

        List<CpuSpecificationModel> specifications = extractSpecifications(specificationLinks);
        System.out.println("[INFO] Extracted " + specifications.size() + " CPU Specifications.");

        CpuSpecificationWriter.writeCsvFile(specifications, outputDir + outputFile);
        System.out.println("[INFO] Finished Intel Web Scraper. Output at: " + execDir + outputDir + outputFile);
    }

    private static List<String> extractSpecificationLinks(List<String> seriesLinks) {
        List<String> specificationLinks = new ArrayList<>();
        for (String link : seriesLinks) {
            String fullLink = "https://ark.intel.com" + link;
            try {
                specificationLinks.addAll(CpuSeriesParser.extractSpecificationLinks(fullLink));
            } catch (Exception e) {
                System.out.println("[" + e.getClass().getSimpleName() + "]: " + e.getMessage() + " (" + fullLink + ")");
            }
        }
        return specificationLinks;
    }

    private static List<CpuSpecificationModel> extractSpecifications(List<String> specificationLinks) {
        List<CpuSpecificationModel> specifications = new ArrayList<>();
        for (String link : specificationLinks) {
            String fullLink = "https://ark.intel.com" + link;
            try {
                specifications.add(CpuSpecificationParser.extractSpecification(fullLink));
                if (specifications.size() % 250 == 0) {
                    System.out.println("[PROGRESS] Extracted " + specifications.size() + " CPU Specifications.");
                }
            } catch (Exception e) {
                System.out.println("[" + e.getClass().getSimpleName() + "]: " + e.getMessage() + " (" + fullLink + ")");
            }
        }
        return specifications;
    }
}
