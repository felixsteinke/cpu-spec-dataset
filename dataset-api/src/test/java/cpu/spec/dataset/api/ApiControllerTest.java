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

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getCpuSpecification() throws Exception {
        String cpuName = "Intel i7";
        mockMvc.perform(
                        get("/api/cpu-dataset/{name}", cpuName)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.cpuName").value(cpuName));
    }

    @Test
    void getCpuSpecifications() throws Exception {
        String cpuName = "Intel i7";
        String cpuNameConditions = "?contains=i7&contains=Intel";
        mockMvc.perform(
                        get("/api/cpu-dataset" + cpuNameConditions)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].cpuName").value(cpuName));
    }
}
