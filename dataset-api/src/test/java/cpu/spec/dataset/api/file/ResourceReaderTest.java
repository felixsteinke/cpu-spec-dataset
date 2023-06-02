package cpu.spec.dataset.api.file;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ResourceReaderTest {

    @Test
    void getAmdColumns() throws IOException {
        Map<Integer, String> columnMap = ResourceReader.getAmdColumns();
        assertNotNull(columnMap);
        assertFalse(columnMap.isEmpty(), "columnMap is empty");
    }

    @Test
    void getIntelColumns() throws IOException {
        Map<Integer, String> columnMap = ResourceReader.getIntelColumns();
        assertNotNull(columnMap);
        assertFalse(columnMap.isEmpty(), "columnMap is empty");
    }

    @Test
    void getCpuBenchmarkColumns() throws IOException {
        Map<Integer, String> columnMap = ResourceReader.getCpuBenchmarkColumns();
        assertNotNull(columnMap);
        assertFalse(columnMap.isEmpty(), "columnMap is empty");
    }

    @Test
    void getAmdDataset() throws IOException {
        Stream<String> actual = ResourceReader.getAmdDataset();
        assertNotNull(actual);
        assertFalse(actual.toList().isEmpty(), "stream is empty");
    }

    @Test
    void getIntelDataset() throws IOException {
        Stream<String> actual = ResourceReader.getIntelDataset();
        assertNotNull(actual);
        assertFalse(actual.toList().isEmpty(), "stream is empty");
    }

    @Test
    void getCpuBenchmarkDataset() throws IOException {
        Stream<String> actual = ResourceReader.getCpuBenchmarkDataset();
        assertNotNull(actual);
        assertFalse(actual.toList().isEmpty(), "stream is empty");
    }
}
