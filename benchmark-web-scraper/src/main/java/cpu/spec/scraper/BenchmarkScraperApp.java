package cpu.spec.scraper;

import cpu.spec.scraper.factory.LoggerFactory;
import cpu.spec.scraper.file.CsvReader;
import cpu.spec.scraper.file.CsvWriter;
import cpu.spec.scraper.file.DatasetOutput;
import cpu.spec.scraper.parser.MegaPageParser;
import cpu.spec.scraper.utils.FileUtils;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class BenchmarkScraperApp {
    private static final Logger LOGGER = LoggerFactory.getLogger();

    public static void main(String[] args) throws Exception {
        LOGGER.info("Starting Cpu Benchmark Scraper.");
        String outputDir = FileUtils.getOutputDirectoryPath("dataset");
        String outputFile = "benchmark-cpus.csv";

        // cpus keep the names (ids of the dataset api) and missing values of the previous dataset version
        Map<String, Map<String, String>> previousRecords = MegaPageParser.previousRecords(CsvReader.read(Path.of(outputDir, outputFile)));
        List<CpuSpecificationModel> specifications = MegaPageParser.extractSpecifications(previousRecords);
        LOGGER.info("Extracted " + specifications.size() + " CPU Specifications.");

        Path output = DatasetOutput.write(outputDir, outputFile, file -> CsvWriter.write(file, CpuSpecificationModel.HEADER,
                specifications.stream().map(CpuSpecificationModel::toRow).toList()));
        LOGGER.info("Finished Cpu Benchmark Scraper. Output at: " + output);
    }
}
