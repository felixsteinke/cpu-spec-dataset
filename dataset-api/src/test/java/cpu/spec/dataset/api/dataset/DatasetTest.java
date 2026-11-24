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
    void getColumnMapping() throws IOException {
        var mapping = dataset.getColumnMapping();
        assertNotNull(mapping);
        assertTrue(mapping.name.length > 0, "name column is mapped");
    }

    @Test
    void allMappedColumnNamesExistInDataset() throws IOException {
        // fails if a regenerated dataset renamed or dropped a mapped column
        var unresolved = dataset.getUnresolvedColumnNames();
        assertTrue(unresolved.isEmpty(), dataset.getName() + " is missing mapped columns: " + unresolved);
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
        columns.values().forEach(header -> {
            assertFalse(header.startsWith("﻿"), "header contains BOM");
            assertFalse(header.startsWith("\""), "header is quoted: " + header);
        });
    }

    @Test
    void normalizeHeader() {
        assertEquals("intelturboboosttechnology20frequency",
                Dataset.normalizeHeader("Intel® Turbo Boost Technology 2.0 Frequency‡"));
        assertEquals("ofcpucores", Dataset.normalizeHeader("\"# of CPU Cores\""));
    }
}
