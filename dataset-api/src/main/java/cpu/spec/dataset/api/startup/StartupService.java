package cpu.spec.dataset.api.startup;

import cpu.spec.dataset.api.CpuSpecification;
import cpu.spec.dataset.api.database.CpuSpecificationRepo;
import cpu.spec.dataset.api.file.ResourceReader;
import cpu.spec.dataset.api.mapping.CsvColumnIndexMapping;
import cpu.spec.dataset.api.mapping.CsvColumnModification;
import cpu.spec.dataset.api.mapping.CsvMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Logger;

@Service
public class StartupService {
    private final Logger LOGGER = Logger.getLogger(StartupService.class.getName());
    private final Boolean deferDbInit;
    private final CpuSpecificationRepo cpuSpecRepo;

    @Autowired
    public StartupService(@Value("${spring.jpa.defer-datasource-initialization}") String deferDbInit,
                          CpuSpecificationRepo cpuSpecRepo) {
        this.deferDbInit = Boolean.parseBoolean(deferDbInit);
        this.cpuSpecRepo = cpuSpecRepo;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void updateDatabaseAfterStartup() {
        if (!deferDbInit) {
            LOGGER.info("Start importing CSV datasets.");
            try {
                List<CpuSpecification> intelSpecs = CsvMapper.mapToObjects(
                                ResourceReader.getIntelDataset(),
                                ResourceReader.getIntelColumns(),
                                CsvColumnIndexMapping.Intel(),
                                CsvColumnModification.Intel())
                        .stream()
                        .peek(spec -> spec.setManufacturer("intel"))
                        .toList();

                List<CpuSpecification> amdSpecs = CsvMapper.mapToObjects(
                                ResourceReader.getAmdDataset(),
                                ResourceReader.getAmdColumns(),
                                CsvColumnIndexMapping.AMD(),
                                CsvColumnModification.AMD())
                        .stream().peek(spec -> {
                            spec.setManufacturer("amd");
                            spec.setSourceUrl("https://www.amd.com/en/products/specifications/processors");
                        }).toList();

                List<CpuSpecification> benchmarkSpecs = CsvMapper.mapToObjects(
                                ResourceReader.getCpuBenchmarkDataset(),
                                ResourceReader.getCpuBenchmarkColumns(),
                                CsvColumnIndexMapping.CpuBenchmark(),
                                CsvColumnModification.CpuBenchmark())
                        .stream()
                        .peek(spec -> {
                            String name = spec.getName().toLowerCase();
                            if (name.contains("intel")) {
                                spec.setManufacturer("intel");
                            } else if (name.contains("amd")) {
                                spec.setManufacturer("amd");
                            }
                        }).toList();

                LOGGER.fine("Saving all objects to the database.");
                cpuSpecRepo.saveAll(intelSpecs);
                cpuSpecRepo.saveAll(amdSpecs);
                cpuSpecRepo.saveAll(benchmarkSpecs);
            } catch (Exception e) {
                e.printStackTrace();
            }
            LOGGER.info("Finished importing CSV datasets.");
        } else {
            LOGGER.info("Skipped CSV dataset import.");
        }
    }
}
