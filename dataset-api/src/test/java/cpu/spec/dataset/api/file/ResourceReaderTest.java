package cpu.spec.dataset.api.file;

import cpu.spec.dataset.api.mapping.CsvColumnIndexMapping;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ResourceReaderTest {

    @Test
    void getAmdMapping() throws IOException {
        CsvColumnIndexMapping actual = ResourceReader.getCsvMapping(ResourceReader.AMD_MAPPING);
        assertNotNull(actual);
    }
}
