package cpu.spec.scraper.file;

import cpu.spec.scraper.CpuSpecificationModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CpuSpecificationWriterTest {

    @Test
    void testWriteCsvFile(@TempDir Path dir) throws IOException {
        CpuSpecificationModel spec = new CpuSpecificationModel();
        spec.id = "Intel-Xeon%208272CL";
        spec.cpuName = "Intel Xeon Platinum 8272CL";
        spec.sourceUrl = "https://www.cpu-world.com/CPUs/Xeon/Intel-Xeon%208272CL.html";
        spec.dataValues.put("The number of CPU cores", "26");
        spec.dataValues.put("Socket", "Socket 3647, LGA3647-0");

        Path file = dir.resolve("cpuworld-cpus.csv");
        CpuSpecificationWriter.writeCsvFile(List.of(spec), file.toString());

        assertEquals(List.of(
                "CpuId,\"CpuName\",Socket,The number of CPU cores,\"SourceUrl\"",
                "Intel-Xeon%208272CL,\"Intel Xeon Platinum 8272CL\",Socket 3647. LGA3647-0,26,\"https://www.cpu-world.com/CPUs/Xeon/Intel-Xeon%208272CL.html\""),
                Files.readAllLines(file));
    }

    @Test
    void testInvalidPath(@TempDir Path dir) {
        CpuSpecificationModel spec = new CpuSpecificationModel();
        assertThrows(IOException.class, () -> CpuSpecificationWriter.writeCsvFile(List.of(spec), dir.toString()));
    }
}
