package cpu.spec.dataset.api.mapping;

import cpu.spec.dataset.api.CpuSpecification;
import cpu.spec.dataset.api.file.ResourceReader;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CsvMapperTest {

    @Test
    void mapAmdDataset() throws IOException {
        List<CpuSpecification> objects = CsvMapper.mapToObjects(
                ResourceReader.getAmdDataset(),
                ResourceReader.getAmdColumns(),
                CsvColumnIndexMapping.AMD(),
                CsvColumnModification.AMD());
        assertNotNull(objects);
        assertFalse(objects.isEmpty(), "objects are empty");
    }

    @Test
    void mapIntelDataset() throws IOException {
        List<CpuSpecification> objects = CsvMapper.mapToObjects(
                ResourceReader.getIntelDataset(),
                ResourceReader.getIntelColumns(),
                CsvColumnIndexMapping.Intel(),
                CsvColumnModification.Intel());
        assertNotNull(objects);
        assertFalse(objects.isEmpty(), "objects are empty");
    }

    @Test
    void mapBenchmarkDataset() throws IOException {
        List<CpuSpecification> objects = CsvMapper.mapToObjects(
                ResourceReader.getCpuBenchmarkDataset(),
                ResourceReader.getCpuBenchmarkColumns(),
                CsvColumnIndexMapping.CpuBenchmark(),
                CsvColumnModification.CpuBenchmark());
        assertNotNull(objects);
        assertFalse(objects.isEmpty(), "objects are empty");
    }
}
