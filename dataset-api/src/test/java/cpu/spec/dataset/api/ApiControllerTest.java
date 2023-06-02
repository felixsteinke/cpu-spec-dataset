package cpu.spec.dataset.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ApiControllerTest {

    /** @noinspection SpringJavaInjectionPointsAutowiringInspection*/
    @Autowired
    private MockMvc mockMvc;

    @Test
    void getNoCpuSpecification() throws Exception {
        String cpuName = "absolutelyNoCPU";
        mockMvc.perform(
                        get("/api/cpu-dataset/{name}", cpuName)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getCpuSpecification() throws Exception {
        String cpuName = "Intel® Core™ i7-6800K Processor";
        mockMvc.perform(
                        get("/api/cpu-dataset/{name}", cpuName)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value(cpuName));
    }

    @Test
    void getCpuSpecifications() throws Exception {
        String cpuName = "Intel® Core™ i7-6800K Processor";
        String cpuNameConditions = "?contains=Intel&contains=Core&contains=i7&contains=6800K&contains=Processor";
        mockMvc.perform(
                        get("/api/cpu-dataset" + cpuNameConditions)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].name").value(cpuName));
    }
    @Test
    void getNoCpuSpecifications() throws Exception {
        String cpuNameConditions = "?contains=absolutelyNoCPU";
        mockMvc.perform(
                        get("/api/cpu-dataset" + cpuNameConditions)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
