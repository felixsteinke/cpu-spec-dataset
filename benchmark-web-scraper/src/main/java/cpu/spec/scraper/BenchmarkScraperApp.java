package cpu.spec.scraper;

import cpu.spec.scraper.factory.LoggerFactory;
import cpu.spec.scraper.file.CpuSpecificationWriter;
import cpu.spec.scraper.parser.CpuSpecificationParser;
import cpu.spec.scraper.parser.MegaPageParser;
import cpu.spec.scraper.utils.FileUtils;
import cpu.spec.scraper.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.logging.Logger;

public class BenchmarkScraperApp {
    private static final Logger LOGGER = LoggerFactory.getLogger();

    public static void main(String[] args) throws Exception {
        LOGGER.info("Starting Cpu Benchmark Scraper.");
        String outputDir = FileUtils.getOutputDirectoryPath("dataset");
        String outputFile = "benchmark-cpus.csv";

        List<String> specificationLinks = MegaPageParser.extractSpecificationLinks();
        LOGGER.info("Extracted " + specificationLinks.size() + " CPU Specification Links.");

        List<CpuSpecificationModel> specifications = extractSpecifications(specificationLinks);
        LOGGER.info("Extracted " + specifications.size() + " CPU Specifications.");

        CpuSpecificationWriter.writeCsvFile(specifications, outputDir + outputFile);
        LOGGER.info("Finished Cpu Benchmark Scraper. Output at: " + outputDir + outputFile);
    }


    private static List<CpuSpecificationModel> extractSpecifications(List<String> specificationLinks) {
        // Number of threads to use
        int NUM_THREADS = 12;
        // setup
        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
        List<CpuSpecificationModel> specifications = new ArrayList<>();
        List<Future<CpuSpecificationModel>> futures = new ArrayList<>();
        // submit extractions
        for (String link : specificationLinks) {
            Callable<CpuSpecificationModel> task = () -> CpuSpecificationParser.extractSpecification(link);
            Future<CpuSpecificationModel> future = executor.submit(task);
            futures.add(future);
        }
        // collect extractions
        for (Future<CpuSpecificationModel> future : futures) {
            try {
                CpuSpecificationModel spec = future.get(); // blocking
                specifications.add(spec);
                if (specifications.size() % 250 == 0) {
                    LOGGER.info(LogUtils.progressMessage(specifications, specificationLinks, "CPU Specifications"));
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
            LOGGER.severe(LogUtils.exceptionMessage(e));
            // e.printStackTrace();
        }
        return specifications;
    }
}
