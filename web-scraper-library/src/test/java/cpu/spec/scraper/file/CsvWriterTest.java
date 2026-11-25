package cpu.spec.scraper.file;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CsvWriterTest {

    @Test
    void quote() {
        assertEquals("\"\"", CsvWriter.quote(null));
        assertEquals("\"Up to 5.7 GHz\"", CsvWriter.quote("Up to 5.7 GHz"));
        assertEquals("\"a, b\"", CsvWriter.quote("a, b"));
        assertEquals("\"say \"\"hi\"\"\"", CsvWriter.quote("say \"hi\""));
        assertEquals("\"line one line two\"", CsvWriter.quote("line one\r\nline two"));
    }

    @Test
    void write(@TempDir Path dir) throws Exception {
        Path file = dir.resolve("test.csv");
        CsvWriter.write(file, List.of("Name", "Cores"), List.of(List.of("AMD Ryzen™ 9 9950X", "16"), Arrays.asList("x", null)));
        assertEquals(List.of("\"Name\",\"Cores\"", "\"AMD Ryzen™ 9 9950X\",\"16\"", "\"x\",\"\""), Files.readAllLines(file));
    }
}
