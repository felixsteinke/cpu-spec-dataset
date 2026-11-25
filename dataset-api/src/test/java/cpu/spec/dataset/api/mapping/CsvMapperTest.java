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
    void amdLaunchYear() {
        var launchDate = new AmdDataset().getColumnModifications().launchDate;
        assertEquals("2023", launchDate.apply("10/19/2023"));
        assertEquals("2016", launchDate.apply("Q3 2016"));
        assertEquals("2021", launchDate.apply("Q12021"));
        assertEquals("2025", launchDate.apply("9/16/25"));
        assertEquals("2017", launchDate.apply("Q217"));
        assertEquals("2018", launchDate.apply("2Q18"));
        assertEquals("2017", launchDate.apply("Channel: 7/27/2017, OEM: 9/5/2016"));
        assertNull(launchDate.apply(""));
    }

    @Test
    void amdTdp() {
        var tdp = new AmdDataset().getColumnModifications().tdp;
        assertEquals("65", tdp.apply("65W"));
        assertEquals("120", tdp.apply("45-120W"));
        assertEquals("25", tdp.apply("15/25W"));
        assertEquals("120", tdp.apply("115 , 120W"));
    }

    @Test
    void ampereNames() {
        var name = new AmpereDataset().getColumnModifications().name;
        assertEquals("Ampere Altra M128-30", name.apply("M128-30"), "unchanged name of the previous dataset");
        assertEquals("AmpereOne® A192-32X", name.apply("AmpereOne® A192-32X"));
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
