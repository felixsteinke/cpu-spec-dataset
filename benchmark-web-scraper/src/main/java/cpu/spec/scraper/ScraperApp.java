package cpu.spec.scraper;

import cpu.spec.scraper.factory.LoggerFactory;
import cpu.spec.scraper.file.CpuSpecificationWriter;
import cpu.spec.scraper.parser.CpuListParser;
import cpu.spec.scraper.parser.CpuSpecificationParser;
import cpu.spec.scraper.utils.FileUtils;
import cpu.spec.scraper.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.logging.Logger;

public class ScraperApp {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScraperApp.class);
    private static final String HOST_URL = "https://www.cpubenchmark.net/";

    public static void main(String[] args) throws Exception {
        LOGGER.info("Starting Cpu Benchmark Scraper.");
        String outputDir = FileUtils.getOutputDirectoryPath("dataset");
        String outputFile = "benchmark-cpus.csv";

        List<String> specificationLinks = CpuListParser.extractSpecificationLinks();
        LOGGER.info("Extracted " + specificationLinks.size() + " CPU Specification Links.");

        List<CpuSpecificationModel> specifications = extractSpecifications(specificationLinks);
        LOGGER.info("Extracted " + specifications.size() + " CPU Specifications.");

        CpuSpecificationWriter.writeCsvFile(specifications, outputDir + outputFile);
        LOGGER.info("Finished Cpu Benchmark Scraper. Output at: " + outputDir + outputFile);
    }


    public static List<CpuSpecificationModel> extractSpecifications(List<String> specificationLinks) {
        // Number of threads to use
        int NUM_THREADS = 12;
        // setup
        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
        List<CpuSpecificationModel> specifications = new ArrayList<>();
        List<Future<CpuSpecificationModel>> futures = new ArrayList<>();
        // submit extractions
        for (String link : specificationLinks) {
            String fullLink = HOST_URL + link;
            Callable<CpuSpecificationModel> task = () -> CpuSpecificationParser.extractSpecification(fullLink);
            Future<CpuSpecificationModel> future = executor.submit(task);
            futures.add(future);
        }
        // collect extractions
        for (Future<CpuSpecificationModel> future : futures) {
            try {
                CpuSpecificationModel spec = future.get();
                specifications.add(spec);
                if (specifications.size() % 250 == 0) {
                    LOGGER.info("Extracted " + specifications.size() + " CPU Specifications.");
                }
            } catch (Exception e) {
                LOGGER.warning(LogUtils.exceptionMessage(e));
            }
        }
        // cleanup
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return specifications;
    }
}
