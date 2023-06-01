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
        assertEquals("201900", actual.id);
        assertEquals("Intel® Pentium® Gold G6405T Processor", actual.cpuName);
        assertEquals("https://ark.intel.com/content/www/us/en/ark/products/201900/intel-pentium-gold-g6405t-processor-4m-cache-3-50-ghz.html", actual.sourceUrl);
        assertEquals("Intel® Pentium® Gold Processor Series", actual.dataValues.get("ProductGroup"));
        assertEquals("2", actual.dataValues.get("CoreCount"));
        assertEquals("4", actual.dataValues.get("ThreadCount"));
        assertEquals("3.50 GHz", actual.dataValues.get("ClockSpeed"));
        assertNull(actual.dataValues.get("ClockSpeedMax"));
        assertEquals("35 W", actual.dataValues.get("MaxTDP"));
        assertEquals("Q1'21", actual.dataValues.get("BornOnDate"));
        assertEquals("128 GB", actual.dataValues.get("MaxMem"));
        assertEquals("PC/Client/Tablet", actual.dataValues.get("CertifiedUseConditions"));
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
