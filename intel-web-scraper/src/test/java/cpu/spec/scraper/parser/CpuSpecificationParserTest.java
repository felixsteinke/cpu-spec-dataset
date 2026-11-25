package cpu.spec.scraper.parser;

import cpu.spec.scraper.CpuSpecificationModel;
import cpu.spec.scraper.exception.ElementNotFoundException;
import org.jsoup.Jsoup;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class CpuSpecificationParserTest {
    private static final String URL = "https://intel.com/content/www/us/en/products/sku/201900/intel-pentium-gold-g6405t-processor-4m-cache-3-50-ghz/specifications.html";

    @Test
    void parseSpecification() throws ElementNotFoundException {
        // structure of a specification page (shortened)
        String html = """
                <div class="product-details"><h1><span itemprop="name">Intel® Pentium® Gold G6405T Processor</span></h1></div>
                <div id="specs-1-0-1">
                  <div class="tech-section"><div class="tech-label"><span>Total Cores</span></div><div class="tech-data"><span>2</span></div></div>
                  <div class="tech-section"><div class="tech-label">Processor Base Frequency</div><div class="tech-data"> 3.50 GHz </div></div>
                  <div class="tech-section"><div class="tech-label">Launch Date</div><div class="tech-data">Q1'21</div></div>
                  <div class="tech-section"><div class="tech-label">Datasheet</div></div>
                </div>""";
        CpuSpecificationModel spec = CpuSpecificationParser.parseSpecification(Jsoup.parse(html), URL);
        assertEquals("201900", spec.id);
        assertEquals("Intel® Pentium® Gold G6405T Processor", spec.cpuName);
        assertEquals(URL, spec.sourceUrl);
        assertEquals("2", spec.dataValues.get("Total Cores"));
        assertEquals("3.50 GHz", spec.dataValues.get("Processor Base Frequency"));
        assertEquals("Q1'21", spec.dataValues.get("Launch Date"));
        assertFalse(spec.dataValues.containsKey("Datasheet"), "label without value");
    }

    @Test
    void parseChangedPageStructure() {
        String withoutSpecs = "<div class=\"product-details\"><span itemprop=\"name\">Intel® Core™ i9</span></div>";
        assertThrows(ElementNotFoundException.class, () -> CpuSpecificationParser.parseSpecification(Jsoup.parse(withoutSpecs), URL));
        assertThrows(ElementNotFoundException.class, () -> CpuSpecificationParser.parseSpecification(Jsoup.parse("<h1>Intel</h1>"), URL));
    }

    @Test
    void testExtractSpecificationSample() throws ElementNotFoundException, IOException {
        CpuSpecificationModel actual = CpuSpecificationParser.extractSpecification(URL);
        assertEquals("201900", actual.id);
        assertEquals("Intel® Pentium® Gold G6405T Processor", actual.cpuName);
        assertEquals(URL, actual.sourceUrl);
        assertEquals("Intel® Pentium® Gold Processor Series", actual.dataValues.get("Product Collection"));
        assertEquals("2", actual.dataValues.get("Total Cores"));
        assertEquals("4", actual.dataValues.get("Total Threads"));
        assertEquals("3.50 GHz", actual.dataValues.get("Processor Base Frequency"));
        assertEquals("35 W", actual.dataValues.get("TDP"));
        assertEquals("Q1'21", actual.dataValues.get("Launch Date"));
        assertEquals("128 GB", actual.dataValues.get("Max Memory Size (dependent on memory type)"));
        assertEquals("Desktop", actual.dataValues.get("Vertical Segment"));
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
