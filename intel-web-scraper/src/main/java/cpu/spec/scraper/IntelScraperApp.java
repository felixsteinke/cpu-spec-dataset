package cpu.spec.scraper;

import cpu.spec.scraper.exception.DirectoryNotFoundException;
import cpu.spec.scraper.exception.ElementNotFoundException;
import cpu.spec.scraper.factory.LoggerFactory;
import cpu.spec.scraper.file.CpuSpecificationWriter;
import cpu.spec.scraper.file.DatasetOutput;
import cpu.spec.scraper.parser.CpuProductParser;
import cpu.spec.scraper.parser.CpuSeriesParser;
import cpu.spec.scraper.parser.CpuSpecificationParser;
import cpu.spec.scraper.utils.FileUtils;
import cpu.spec.scraper.utils.LogUtils;
import me.tongfei.progressbar.ProgressBar;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public class IntelScraperApp {
    private static final Logger LOGGER = LoggerFactory.getLogger();
    private static final String HOST_URL = "https://intel.com";
    /**
     * abort if this many specification pages fail in a row (e.g. requests are blocked)
     */
    private static final int MAX_CONSECUTIVE_FAILURES = 25;
    private static final long RETRY_DELAY_MS = 2000;

    public static void main(String[] args) throws ElementNotFoundException, IOException, DirectoryNotFoundException {
        LOGGER.info("Starting Intel Scraper.");
        String outputDir = FileUtils.getOutputDirectoryPath("dataset");
        String outputFile = "intel-cpus.csv";

        List<String> seriesLinks = CpuProductParser.extractSeriesLinks();
        LOGGER.info("Extracted " + seriesLinks.size() + " CPU Series Links.");

        List<String> specificationLinks = ScraperConfig.limit(extractSpecificationLinks(seriesLinks));
        LOGGER.info("Extracted " + specificationLinks.size() + " CPU Specification Links.");

        List<CpuSpecificationModel> specifications = extractSpecifications(specificationLinks);
        LOGGER.info("Extracted " + specifications.size() + " of " + specificationLinks.size() + " CPU Specifications.");
        if (specifications.isEmpty()) {
            throw new IllegalStateException("No CPU Specifications extracted (blocked requests or changed website structure).");
        }

        Path output = DatasetOutput.resolve(outputDir, outputFile, specifications.size());
        CpuSpecificationWriter.writeCsvFile(specifications, output.toString());
        LOGGER.info("Finished Intel Scraper. Output at: " + output);
    }

    private static List<String> extractSpecificationLinks(List<String> seriesLinks) {
        Set<String> specificationLinks = new LinkedHashSet<>();
        for (String link : seriesLinks) {
            String fullLink = absolute(link);
            try {
                specificationLinks.addAll(CpuSeriesParser.extractSpecificationLinks(fullLink));
            } catch (Exception e) {
                LOGGER.warning(LogUtils.exceptionMessage(e, fullLink));
            }
            ScraperConfig.pause(0);
        }
        return new ArrayList<>(specificationLinks);
    }

    private static List<CpuSpecificationModel> extractSpecifications(List<String> specificationLinks) {
        List<CpuSpecificationModel> specifications = new ArrayList<>();
        int failures = 0;
        int consecutiveFailures = 0;
        try (ProgressBar progressBar = new ProgressBar("Extracting specifications:", specificationLinks.size())) {
            for (String link : specificationLinks) {
                String fullLink = absolute(link);
                CpuSpecificationModel specification = extractWithRetry(fullLink);
                if (specification != null) {
                    specifications.add(specification);
                    consecutiveFailures = 0;
                } else {
                    failures++;
                    if (++consecutiveFailures >= MAX_CONSECUTIVE_FAILURES) {
                        LOGGER.severe("Aborting after " + consecutiveFailures + " failed specification pages in a row (requests blocked?).");
                        break;
                    }
                }
                progressBar.step();
                ScraperConfig.pause(0);
            }
        }
        if (failures > 0) {
            LOGGER.warning(failures + " specification pages could not be extracted.");
        }
        return specifications;
    }

    private static CpuSpecificationModel extractWithRetry(String url) {
        for (int attempt = 1; attempt <= 2; attempt++) {
            try {
                return CpuSpecificationParser.extractSpecification(url);
            } catch (Exception e) {
                LOGGER.warning("Attempt " + attempt + ": " + LogUtils.exceptionMessage(e, url));
                if (attempt == 1) {
                    ScraperConfig.sleep(RETRY_DELAY_MS);
                }
            }
        }
        return null;
    }

    private static String absolute(String link) {
        return link.startsWith("http") ? link : HOST_URL + link;
    }
}
