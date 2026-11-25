package cpu.spec.scraper.file;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

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
        assertEquals(dataset(), write("cpu0", "cpu1"));
        assertEquals(List.of("cpu0", "cpu1"), names(dataset()));
    }

    @Test
    void runIsMergedIntoDataset() throws Exception {
        writeDataset(10);
        assertEquals(dataset(), write("cpu8", "cpu10"));
        List<String> expected = IntStream.rangeClosed(0, 10).mapToObj(i -> "cpu" + i).toList();
        assertEquals(expected, names(dataset()), "cpus the run did not find are kept, new ones appended");
    }

    @Test
    void forcedRunReplacesDataset() throws Exception {
        writeDataset(10);
        System.setProperty("scraper.force", "true");
        assertEquals(dataset(), write("cpu8", "cpu10"));
        assertEquals(List.of("cpu8", "cpu10"), names(dataset()));
    }

    @Test
    void emptyRunKeepsDataset() throws Exception {
        writeDataset(10);
        assertThrows(IllegalStateException.class, () -> write());
        assertEquals(10, names(dataset()).size());
    }

    @Test
    void smokeTestNeverChangesDataset() throws Exception {
        writeDataset(10);
        System.setProperty("scraper.limit", "2");
        assertEquals(dir.resolve("x-cpus.limited.csv"), write("cpu0", "cpu10"));
        assertEquals(List.of("cpu0", "cpu10"), names(dir.resolve("x-cpus.limited.csv")));
        assertEquals(10, names(dataset()).size());
    }

    private Path write(String... names) throws Exception {
        List<List<String>> rows = List.of(names).stream().map(List::of).toList();
        return DatasetOutput.write(dir.toString(), "x-cpus.csv", file -> CsvWriter.write(file, List.of("CpuName"), rows));
    }

    private Path dataset() {
        return dir.resolve("x-cpus.csv");
    }

    private static List<String> names(Path file) throws Exception {
        return CsvReader.read(file).stream().map(row -> row.get("CpuName")).toList();
    }

    private void writeDataset(int rows) throws Exception {
        StringBuilder csv = new StringBuilder("CpuName\n");
        for (int i = 0; i < rows; i++) csv.append("cpu").append(i).append('\n');
        Files.writeString(dataset(), csv);
    }
}
