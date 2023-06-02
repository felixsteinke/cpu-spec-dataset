package cpu.spec.scraper.parser;

import cpu.spec.scraper.CpuSpecificationModel;
import cpu.spec.scraper.exception.ElementNotFoundException;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CpuSpecificationParserTest {

    @Test
    void testExtractSpecificationSample() throws ElementNotFoundException, IOException {
        CpuSpecificationModel actual = CpuSpecificationParser.extractSpecification("https://www.cpubenchmark.net/cpu.php?cpu=Intel+Xeon+Platinum+8173M+@+2.00GHz&id=3182");
        assertEquals("Intel Xeon Platinum 8173M @ 2.00GHz", actual.cpuName);
        assertEquals("https://www.cpubenchmark.net/cpu.php?cpu=Intel+Xeon+Platinum+8173M+@+2.00GHz&id=3182", actual.sourceUrl);
    }

    @Test
    void testNonExistingUrl() {
        assertThrows(IOException.class, () -> CpuSpecificationParser.extractSpecification("https://not-a-website"));
    }

    @Test
    void testInvalidUrl() {
        assertThrows(ElementNotFoundException.class, () -> CpuSpecificationParser.extractSpecification("https://google.com"));
    }
}
