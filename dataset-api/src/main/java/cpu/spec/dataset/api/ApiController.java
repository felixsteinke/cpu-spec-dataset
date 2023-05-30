package cpu.spec.dataset.api;

import cpu.spec.dataset.api.database.CpuSpecificationRepo;
import cpu.spec.dataset.api.exception.NonUniqueResultException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
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

    @GetMapping(path = "/equals/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CpuSpecification> getCpuSpecificationEquals(@PathVariable("name") String cpuName) {
        LOGGER.info("Request cpu specification equals (cpu=" + cpuName + ")");
        return ResponseEntity.ok(cpuSpecRepo.findById(cpuName).orElseThrow());
    }

    @GetMapping(path = "/like/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CpuSpecification>> getCpuSpecificationsLike(@PathVariable("name") String cpuName) {
        LOGGER.info("Request cpu specifications like (cpu=" + cpuName + ")");
        return ResponseEntity.ok(cpuSpecRepo.findByName(cpuName));
    }

    @GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CpuSpecification> getCpuSpecificationByConditions(@RequestParam("condition") List<String> cpuNameConditions) throws NonUniqueResultException {
        LOGGER.info("Request cpu specification with (conditions=" + cpuNameConditions + ")");
        List<String> conditions = cpuNameConditions.stream().map(String::toLowerCase).toList();
        List<String> results = new ArrayList<>();
        for (String cpuName: cpuSpecRepo.findDistinctNames()) {
            boolean isResult = true;
            String name = cpuName.toLowerCase();
            for (String condition: conditions) {
                if (!name.contains(condition)){
                    isResult = false;
                    break;
                }
            }
            if (isResult){
                results.add(cpuName);
            }
        }
        if (results.size() == 1){
            return ResponseEntity.ok(cpuSpecRepo.findById(results.get(0)).orElseThrow());
        } else if (results.size() == 0){
            throw new NoSuchElementException("Conditions dont match any name.");
        } else {
            throw new NonUniqueResultException(results);
        }
    }
}
