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
        spec.id = "201900";
        spec.cpuName = "Intel® Pentium® Gold G6405T Processor";
        spec.sourceUrl = "https://intel.com/content/www/us/en/products/sku/201900/x/specifications.html";
        spec.dataValues.put("Total Cores", "2");
        spec.dataValues.put("Launch Date", "Q1'21");

        Path file = dir.resolve("intel-cpus.csv");
        CpuSpecificationWriter.writeCsvFile(List.of(spec), file.toString());

        assertEquals(List.of(
                "CpuId,\"CpuName\",Launch Date,Total Cores,\"SourceUrl\"",
                "201900,\"Intel® Pentium® Gold G6405T Processor\",Q1'21,2,\"https://intel.com/content/www/us/en/products/sku/201900/x/specifications.html\""),
                Files.readAllLines(file));
    }

    @Test
    void testInvalidPath(@TempDir Path dir) {
        CpuSpecificationModel spec = new CpuSpecificationModel();
        assertThrows(IOException.class, () -> CpuSpecificationWriter.writeCsvFile(List.of(spec), dir.toString()));
    }
}
