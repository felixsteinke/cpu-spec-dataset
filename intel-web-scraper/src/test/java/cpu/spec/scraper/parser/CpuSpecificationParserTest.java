package cpu.spec.scraper.parser;

import cpu.spec.scraper.CpuSpecificationModel;
import cpu.spec.scraper.exception.ElementNotFoundException;
import org.jsoup.Jsoup;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class CpuSpecificationParserTest {
    private static final String URL = "https://intel.com/content/www/us/en/products/sku/201900/intel-pentium-gold-g6405t-processor-4m-cache-3-50-ghz/specifications.html";

    @Test
    void parseSpecification() throws ElementNotFoundException {
        // structure of a specification page (shortened)
        String html = """
                <h1 class="cmp-teaser__title">Intel® Pentium® Gold G6405T Processor<span class="cmp-teaser__description">Specifications</span></h1>
                <div class="cmp-list-section__section" id="list-section-spec-0">
                  <div class="cmp-title"><h2 class="cmp-title__text">Essentials</h2></div>
                  <table class="cmp-list-section__table"><tbody>
                    <tr><th scope="row" class="key"><div class="row-key"> Total Cores <button class="info-icon" data-info-title="Total Cores"></button></div></th>
                        <td class="value"><div class="row-value"> 2 <button class="copy-icon" data-copy-text="2"></button></div></td></tr>
                    <tr><th scope="row" class="key"><div class="row-key">Processor Base Frequency</div></th><td class="value"><div class="row-value"> 3.50 GHz </div></td></tr>
                    <tr><th scope="row" class="key"><div class="row-key">GPU Name<small><sup>‡</sup></small></div></th><td class="value"><div class="row-value">Intel® UHD Graphics 610</div></td></tr>
                    <tr><th scope="row" class="key"><div class="row-key">T<sub>JUNCTION</sub></div></th><td class="value"><div class="row-value">100°C</div></td></tr>
                    <tr><th scope="row" class="key"><div class="row-key">Datasheet</div></th></tr>
                  </tbody></table>
                </div>
                <div class="cmp-list-section__section" id="list-section-spec-1">
                  <table class="cmp-list-section__table"><tbody>
                    <tr><th scope="row" class="key"><div class="row-key">Launch Date</div></th><td class="value"><div class="row-value">Q1'21</div></td></tr>
                  </tbody></table>
                </div>""";
        CpuSpecificationModel spec = CpuSpecificationParser.parseSpecification(Jsoup.parse(html), URL);
        assertEquals("201900", spec.id);
        assertEquals("Intel® Pentium® Gold G6405T Processor", spec.cpuName);
        assertEquals(URL, spec.sourceUrl);
        assertEquals("2", spec.dataValues.get("Total Cores"));
        assertEquals("3.50 GHz", spec.dataValues.get("Processor Base Frequency"));
        assertEquals("Intel® UHD Graphics 610", spec.dataValues.get("GPU Name‡"), "footnote mark without space (dataset header)");
        assertEquals("100°C", spec.dataValues.get("TJUNCTION"), "subscript without space (dataset header)");
        assertEquals("Q1'21", spec.dataValues.get("Launch Date"));
        assertFalse(spec.dataValues.containsKey("Datasheet"), "label without value");
    }

    @Test
    void parseChangedPageStructure() {
        String withoutSpecs = "<h1 class=\"cmp-teaser__title\">Intel® Core™ i9<span class=\"cmp-teaser__description\">Specifications</span></h1>";
        assertThrows(ElementNotFoundException.class, () -> CpuSpecificationParser.parseSpecification(Jsoup.parse(withoutSpecs), URL));
        assertThrows(ElementNotFoundException.class, () -> CpuSpecificationParser.parseSpecification(Jsoup.parse("<h1>Intel</h1>"), URL));
    }

    @Tag("live")
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

    @Tag("live")
    @Test
    void testNonExistingUrl() {
        assertThrows(IOException.class, () -> CpuSpecificationParser.extractSpecification("https://not-a-website"));
    }

    @Tag("live")
    @Test
    void testInvalidUrl() {
        assertThrows(ElementNotFoundException.class, () -> CpuSpecificationParser.extractSpecification("https://google.com"));
    }
}
