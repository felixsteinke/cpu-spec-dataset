package cpu.spec.dataset.api.mapping;

import cpu.spec.dataset.api.CpuSpecification;
import cpu.spec.dataset.api.file.ResourceReader;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class CsvMapperTest {

    @Test
    void mapAmdDataset() throws IOException {
        List<CpuSpecification> actual = CsvMapper.mapToObjects(
                ResourceReader.getAmdDataset(),
                CsvColumnIndexMapping.AMD(),
                CsvColumnModification.AMD());
        assertNotNull(actual);
    }

    @Test
    void mapIntelDataset() throws IOException {
        List<CpuSpecification> actual = CsvMapper.mapToObjects(
                ResourceReader.getIntelDataset(),
                CsvColumnIndexMapping.Intel(),
                CsvColumnModification.Intel());
        assertNotNull(actual);
    }
}
