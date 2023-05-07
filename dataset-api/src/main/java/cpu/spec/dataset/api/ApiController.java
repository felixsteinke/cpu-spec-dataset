package cpu.spec.dataset.api;

import cpu.spec.dataset.api.database.CpuSpecificationRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/cpu-dataset")
public class ApiController {
    private final Logger LOGGER = Logger.getLogger(ApiController.class.getName());
    private final CpuSpecificationRepo cpuSpecRepo;

    @Autowired
    public ApiController(CpuSpecificationRepo cpuSpecRepo) {
        this.cpuSpecRepo = cpuSpecRepo;
    }

    @GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CpuSpecification> getCpuSpecification(@RequestParam("cpu") String cpuName) {
        LOGGER.info("Request cpu specification (cpu=" + cpuName + ")");
        return ResponseEntity.ok(cpuSpecRepo.findById(cpuName).orElseThrow());
    }
}
