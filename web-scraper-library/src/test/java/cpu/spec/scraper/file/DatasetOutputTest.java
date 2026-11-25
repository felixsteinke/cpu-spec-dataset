package cpu.spec.scraper.file;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DatasetOutputTest {
    @TempDir
    Path dir;

    @AfterEach
    void clearProperties() {
        System.clearProperty("scraper.force");
        System.clearProperty("scraper.limit");
    }

    @Test
    void newDatasetIsWritten() throws Exception {
        assertEquals(dir.resolve("x-cpus.csv"), DatasetOutput.resolve(dir.toString(), "x-cpus.csv", 3));
    }

    @Test
    void completeRunReplacesDataset() throws Exception {
        writeDataset(10);
        assertEquals(dir.resolve("x-cpus.csv"), DatasetOutput.resolve(dir.toString(), "x-cpus.csv", 9));
    }

    @Test
    void incompleteRunKeepsDataset() throws Exception {
        writeDataset(10);
        assertEquals(dir.resolve("x-cpus.incomplete.csv"), DatasetOutput.resolve(dir.toString(), "x-cpus.csv", 7));
    }

    @Test
    void forcedRunReplacesDataset() throws Exception {
        writeDataset(10);
        System.setProperty("scraper.force", "true");
        assertEquals(dir.resolve("x-cpus.csv"), DatasetOutput.resolve(dir.toString(), "x-cpus.csv", 1));
    }

    @Test
    void smokeTestNeverReplacesDataset() throws Exception {
        writeDataset(10);
        System.setProperty("scraper.limit", "2");
        assertEquals(dir.resolve("x-cpus.limited.csv"), DatasetOutput.resolve(dir.toString(), "x-cpus.csv", 2));
    }

    private void writeDataset(int rows) throws Exception {
        StringBuilder csv = new StringBuilder("Name\n");
        for (int i = 0; i < rows; i++) csv.append("cpu").append(i).append('\n');
        Files.writeString(dir.resolve("x-cpus.csv"), csv);
    }
}
