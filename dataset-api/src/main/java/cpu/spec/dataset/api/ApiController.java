package cpu.spec.dataset.api;

import cpu.spec.dataset.api.database.CpuSpecificationRepo;
import cpu.spec.dataset.api.mapping.CsvColumnIndexMapping;
import cpu.spec.dataset.api.mapping.CsvColumnModification;
import cpu.spec.dataset.api.mapping.CsvMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

import static cpu.spec.dataset.api.file.ResourceReader.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/cpu-dataset")
public class ApiController {
    private final Logger LOGGER = Logger.getLogger(ApiController.class.getName());
    private final Boolean deferDbInit;
    private final CpuSpecificationRepo cpuSpecRepo;

    @Autowired
    public ApiController(@Value("${spring.jpa.defer-datasource-initialization}") String deferDbInit,
                         CpuSpecificationRepo cpuSpecRepo) {
        this.deferDbInit = Boolean.parseBoolean(deferDbInit);
        this.cpuSpecRepo = cpuSpecRepo;
    }

    @GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CpuSpecification> getCpuSpecification(@RequestParam("cpu") String cpuName) {
        LOGGER.info("Request cpu specification (cpu=" + cpuName + ")");
        return ResponseEntity.ok(cpuSpecRepo.findById(cpuName).orElseThrow());
    }

    @EventListener(ApplicationReadyEvent.class)
    public void updateDatabaseAfterStartup() {
        if (!deferDbInit) {
            LOGGER.info("Start importing CSV Datasets");
            try {
                CsvColumnIndexMapping intelMapping = getCsvMapping(INTEL_MAPPING);
                CsvColumnModification intelModification = new CsvColumnModification();
                CsvColumnIndexMapping amdMapping = getCsvMapping(AMD_MAPPING);
                CsvColumnModification amdModification = new CsvColumnModification();
                cpuSpecRepo.saveAll(CsvMapper.mapToObjects(getCsvLines(INTEL_DATASET_CSV), intelMapping, intelModification));
                cpuSpecRepo.saveAll(CsvMapper.mapToObjects(getCsvLines(AMD_DATASET_CSV), amdMapping, amdModification));
            } catch (Exception e) {
                e.printStackTrace();
            }
            LOGGER.info("Finished importing CSV Datasets");
        }
    }
}
