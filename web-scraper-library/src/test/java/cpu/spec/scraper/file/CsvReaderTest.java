package cpu.spec.scraper.file;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CsvReaderTest {

    @Test
    void parseLine() {
        assertEquals(List.of("AMD Ryzen 5 2600", "AM4", "", "3.9 GHz"), CsvReader.parseLine("AMD Ryzen 5 2600,AM4,,3.9 GHz"));
        assertEquals(List.of("AM2,AM2+", "say \"hi\"", "x"), CsvReader.parseLine("\"AM2,AM2+\",\"say \"\"hi\"\"\",x"));
        // unquoted fields of the intel and cpu-world datasets can contain quotes
        assertEquals(List.of("2.99\" x 2.22\"", "the \"N4200\" page", "x"), CsvReader.parseLine("2.99\" x 2.22\",the \"N4200\" page,x"));
    }

    @Test
    void readWrittenFile(@TempDir Path dir) throws Exception {
        Path file = dir.resolve("x.csv");
        CsvWriter.write(file, List.of("CpuName", "Socket"), List.of(List.of("[Dual CPU] AMD EPYC 7252", "SP3")));
        assertEquals(List.of(Map.of("CpuName", "[Dual CPU] AMD EPYC 7252", "Socket", "SP3")), CsvReader.read(file));
        assertEquals(List.of(), CsvReader.read(dir.resolve("missing.csv")));
    }

    @Test
    void readWithBom(@TempDir Path dir) throws Exception {
        Path file = dir.resolve("bom.csv");
        Files.writeString(file, "﻿\"Name\",\"Cores\"\n\"AMD Ryzen 9\",\"16\"\n");
        assertEquals("16", CsvReader.read(file).getFirst().get("Cores"));
        assertEquals(List.of("Name", "Cores"), CsvReader.readHeader(file));
        assertEquals(List.of(), CsvReader.readHeader(dir.resolve("missing.csv")));
    }
}
