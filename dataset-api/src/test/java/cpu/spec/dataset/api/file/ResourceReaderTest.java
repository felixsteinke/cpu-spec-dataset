package cpu.spec.dataset.api.file;

import cpu.spec.dataset.api.database.CsvColumnMapping;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ResourceReaderTest {

    @Test
    void getAmdCsvMapping() throws IOException {
        List<CsvColumnMapping> actual = ResourceReader.getAmdCsvMapping();
        assertTrue(actual.size() > 5, "size > 5");
    }

    @Test
    void getIntelCsvMapping() throws IOException {
        List<CsvColumnMapping> actual = ResourceReader.getIntelCsvMapping();
        assertTrue(actual.size() > 5, "size > 5");
    }
}
