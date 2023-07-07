package cpu.spec.dataset.api.mapping;

import cpu.spec.dataset.api.CpuSpecification;
import cpu.spec.dataset.api.registry.AmdDataset;
import cpu.spec.dataset.api.registry.BenchmarkDataset;
import cpu.spec.dataset.api.registry.CpuworldDataset;
import cpu.spec.dataset.api.registry.IntelDataset;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CsvMapperTest {

    @Test
    void mapAmdDataset() throws IOException {
        List<CpuSpecification> objects = CsvMapper.mapToObjects(new AmdDataset());
        assertNotNull(objects);
        assertFalse(objects.isEmpty(), "objects are empty");
    }

    @Test
    void mapIntelDataset() throws IOException {
        List<CpuSpecification> objects = CsvMapper.mapToObjects(new IntelDataset());
        assertNotNull(objects);
        assertFalse(objects.isEmpty(), "objects are empty");
    }

    @Test
    void mapBenchmarkDataset() throws IOException {
        List<CpuSpecification> objects = CsvMapper.mapToObjects(new BenchmarkDataset());
        assertNotNull(objects);
        assertFalse(objects.isEmpty(), "objects are empty");
    }

    @Test
    void mapCpuworldDataset() throws IOException {
        List<CpuSpecification> objects = CsvMapper.mapToObjects(new CpuworldDataset());
        assertNotNull(objects);
        assertFalse(objects.isEmpty(), "objects are empty");
    }
}
