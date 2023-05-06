package cpu.spec.scraper.parser;

import cpu.spec.scraper.CpuSpecificationModel;
import cpu.spec.scraper.exception.ElementNotFoundException;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class CpuSpecificationParserTest {

    @Test
    void testExtractSpecificationSample() throws ElementNotFoundException, IOException {
        CpuSpecificationModel actual = CpuSpecificationParser.extractSpecification("https://ark.intel.com/content/www/us/en/ark/products/201900/intel-pentium-gold-g6405t-processor-4m-cache-3-50-ghz.html");
        assertEquals("Intel® Pentium® Gold G6405T Processor", actual.cpuName);
        assertEquals("intel", actual.manufacturerName);
        assertEquals("Intel® Pentium® Gold Processor Series", actual.productCollection);
        assertEquals("2", actual.totalCores);
        assertEquals("4", actual.totalThreads);
        assertEquals("3.50 GHz", actual.baseFrequency);
        assertNull(actual.turboFrequency);
        assertEquals("35 W", actual.defaultTdp);
        assertEquals("Q1'21", actual.launchDate);
        assertEquals("128 GB", actual.maxRam);
        assertEquals("PC/Client/Tablet", actual.usageType);
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
