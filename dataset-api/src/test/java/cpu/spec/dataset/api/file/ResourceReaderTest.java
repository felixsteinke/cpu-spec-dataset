package cpu.spec.dataset.api.file;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ResourceReaderTest {

    @Test
    void getAmdDataset() throws IOException {
        Stream<String> actual = ResourceReader.getAmdDataset();
        assertNotNull(actual);
    }

    @Test
    void getIntelDataset() throws IOException {
        Stream<String> actual = ResourceReader.getIntelDataset();
        assertNotNull(actual);
    }
}
