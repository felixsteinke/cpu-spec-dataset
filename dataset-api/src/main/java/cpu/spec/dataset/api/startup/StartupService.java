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
import java.util.stream.Collectors;

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
            LOGGER.info("Start importing CSV Datasets");
            try {
                List<CpuSpecification> intelSpecs = CsvMapper.mapToObjects(
                        ResourceReader.getIntelDataset(),
                        CsvColumnIndexMapping.Intel(),
                        CsvColumnModification.Intel());
                List<CpuSpecification> amdSpecs = CsvMapper.mapToObjects(
                        ResourceReader.getAmdDataset(),
                        CsvColumnIndexMapping.AMD(),
                        CsvColumnModification.AMD());
                LOGGER.info("Saving all objects to the database");
                cpuSpecRepo.saveAll(intelSpecs);
                cpuSpecRepo.saveAll(amdSpecs);
            } catch (Exception e) {
                e.printStackTrace();
            }
            LOGGER.info("Finished importing CSV Datasets");
        }
    }

    private List<CpuSpecification> enrichAmdData(List<CpuSpecification> amdData) {
        return amdData.stream().peek(obj -> {
            obj.setManufacturer("amd");
            obj.setSourceUrl("https://www.amd.com/en/products/specifications/processors");
        }).collect(Collectors.toList());
    }
}
