package cpu.spec.dataset.api.dataset;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

abstract class DatasetTest {
    private final Dataset dataset;

    protected DatasetTest(Dataset dataset) {
        this.dataset = dataset;
    }

    @Test
    void getName() {
        var name = dataset.getName();
        assertNotNull(name);
        assertTrue(name.contains(".csv"), "name contains '.csv'");
    }

    @Test
    void getColumnMapping() {
        var mapping = dataset.getColumnMapping();
        assertNotNull(mapping);
    }

    @Test
    void getColumnModifications() {
        var modifications = dataset.getColumnModifications();
        assertNotNull(modifications);
    }

    @Test
    void getDatasetLines() throws IOException {
        var lines = dataset.getDatasetLines();
        assertNotNull(lines);
        assertFalse(lines.toList().isEmpty(), "lines is empty");
    }

    @Test
    void getDatasetColumns() throws IOException {
        var columns = dataset.getDatasetColumns();
        assertNotNull(columns);
        assertFalse(columns.isEmpty(), "columns is empty");
    }
}
