package cpu.spec.dataset.api.mapping;

import cpu.spec.dataset.api.CpuSpecification;
import cpu.spec.dataset.api.dataset.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class CsvMapperTest {

    static Stream<Dataset> datasets() {
        return Stream.of(new AmdDataset(), new AmpereDataset(), new BenchmarkDataset(), new CpuworldDataset(), new IntelDataset());
    }

    @ParameterizedTest
    @MethodSource("datasets")
    void mapDataset(Dataset dataset) throws IOException {
        List<CpuSpecification> objects = CsvMapper.mapToObjects(dataset);
        assertNotNull(objects);
        assertFalse(objects.isEmpty(), "objects are empty");
        objects.forEach(spec -> assertNotNull(spec.getName(), dataset.getName() + " contains cpu without name"));
    }

    @ParameterizedTest
    @MethodSource("datasets")
    void mapDatasetFillsCoreValues(Dataset dataset) throws IOException {
        // guards against mappings pointing to wrong columns
        List<CpuSpecification> objects = CsvMapper.mapToObjects(dataset);
        assertMostlyFilled(dataset, objects, "cores", CpuSpecification::getCores);
        assertMostlyFilled(dataset, objects, "baseFrequency", CpuSpecification::getBaseFrequency);
    }

    @Test
    void frequencyConversionKeepsFraction() {
        assertEquals("3700", new IntelDataset().getColumnModifications().baseFrequency.apply("3.70 GHz"));
        assertEquals("5100", new AmdDataset().getColumnModifications().maxFrequency.apply("Up to 5.1 GHz"));
        assertEquals("2200", new BenchmarkDataset().getColumnModifications().baseFrequency.apply("2.2 GHz"));
        assertEquals("3000", new AmpereDataset().getColumnModifications().baseFrequency.apply("3.0"));
    }

    @Test
    void intelLaunchYear() {
        var launchDate = new IntelDataset().getColumnModifications().launchDate;
        assertEquals("2025", launchDate.apply("Q1'25"));
        assertEquals("1999", launchDate.apply("Q4'99"));
    }

    private static void assertMostlyFilled(Dataset dataset, List<CpuSpecification> objects, String field,
                                           Function<CpuSpecification, Object> getter) {
        long filled = objects.stream().map(getter).filter(v -> v != null).count();
        assertTrue(filled * 2 >= objects.size(),
                String.format("%s: only %d of %d entries have '%s'", dataset.getName(), filled, objects.size(), field));
    }
}
