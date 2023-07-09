package cpu.spec.dataset.api.configuration;

import cpu.spec.dataset.api.CpuSpecification;
import cpu.spec.dataset.api.CpuSpecificationRepo;
import cpu.spec.dataset.api.dataset.*;
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
            LOGGER.info("Starting dataset import.");
            List<Dataset> datasets = List.of(
                    new AmdDataset(),
                    new BenchmarkDataset(),
                    new CpuworldDataset(),
                    new IntelDataset(),
                    new AmpereDataset());
            for (Dataset dataset : datasets) {
                try {
                    List<CpuSpecification> cpuSpecs = CsvMapper.mapToObjects(dataset)
                            .stream()
                            .peek(spec -> {
                                String name = spec.getName().toLowerCase();
                                if (name.contains("intel")) {
                                    spec.setManufacturer("intel");
                                } else if (name.contains("amd")) {
                                    spec.setManufacturer("amd");
                                }
                            }).toList();
                    cpuSpecRepo.saveAll(cpuSpecs);
                } catch (Exception e) {
                    LOGGER.severe("Processing " + dataset.getName() + " encountered " + e.getClass().getSimpleName() + ": " + e.getMessage());
                    // e.printStackTrace();
                }
            }
            LOGGER.info("Finished dataset import.");
        } else {
            LOGGER.info("Skipped dataset import.");
        }
    }
}
