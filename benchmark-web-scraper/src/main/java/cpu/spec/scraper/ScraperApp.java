package cpu.spec.scraper;

import cpu.spec.scraper.exception.ElementNotFoundException;
import cpu.spec.scraper.file.CpuSpecificationWriter;
import cpu.spec.scraper.parser.CpuListParser;
import cpu.spec.scraper.parser.CpuSpecificationParser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ScraperApp {
    public static void main(String[] args) throws ElementNotFoundException, IOException {
        String execDir = System.getProperty("user.dir");
        String outputDir = "./dataset/";
        String outputFile = "benchmark-cpus.csv";

        if (!new File(outputDir).exists()) {
            outputDir = "../dataset/";
            if (!new File(outputDir).exists()) {
                System.out.println("[WARNING] Execution directory: " + execDir);
                throw new ElementNotFoundException("Target Directory", outputDir);
            }
        }

        System.out.println("[INFO] Starting CPU Benchmark Web Scraper.");
        List<String> specificationLinks = CpuListParser.extractSpecificationLinks("cpu_mega_page.html");
        System.out.println("[INFO] Extracted " + specificationLinks.size() + " CPU Specification Links.");

        List<CpuSpecificationModel> specifications = extractSpecifications(specificationLinks);
        System.out.println("[INFO] Extracted " + specifications.size() + " CPU Specifications.");

        CpuSpecificationWriter.writeCsvFile(specifications, outputDir + outputFile);
        System.out.println("[INFO] Finished Intel Web Scraper. Output at: " + execDir + outputDir + outputFile);
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
            String fullLink = "https://www.cpubenchmark.net/" + link;
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
                    System.out.println("[PROGRESS] Extracted " + specifications.size() + " CPU Specifications.");
                }
            } catch (Exception e) {
                System.out.println("[" + e.getClass().getSimpleName() + "]: " + e.getMessage());
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
