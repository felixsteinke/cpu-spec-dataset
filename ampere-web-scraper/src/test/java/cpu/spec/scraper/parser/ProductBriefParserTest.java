package cpu.spec.scraper.parser;

import cpu.spec.scraper.CpuSpecificationModel;
import cpu.spec.scraper.exception.ElementNotFoundException;
import org.jsoup.Jsoup;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductBriefParserTest {
    private static final String URL = "https://amperecomputing.com/briefs/test";

    @Test
    void parseSkuTableByColumnNames() throws ElementNotFoundException {
        String html = """
                <table><tr><th>Features</th><th>Benefit</th></tr><tr><td>Memory Tagging</td><td>...</td></tr></table>
                <table>
                  <tr><td>Processor Model</td><td>Core Count</td><td>Freq (GHz)</td><td>Usage Power*(W)</td><td>SIR*</td></tr>
                  <tr><td>AmpereOne® A192-32X</td><td>192</td><td>3.2</td><td>283</td><td>729</td></tr>
                  <tr><td>AmpereOne® A96-36X</td><td>96</td><td>3.6</td><td>184</td><td>434</td></tr>
                </table>""";
        List<CpuSpecificationModel> specs = ProductBriefParser.parseSpecifications(Jsoup.parse(html), "AmpereOne", URL);
        assertEquals(2, specs.size());
        assertEquals(new CpuSpecificationModel("AmpereOne® A192-32X", "AmpereOne", "192", "3.2", "283", URL), specs.getFirst());
    }

    @Test
    void altraModelNamesAreKept() throws ElementNotFoundException {
        String html = """
                <table>
                  <tr><th>PRODUCT NAME</th><th>CORES</th><th>SUSTAINED FREQUENCY (GHz)</th><th>USAGE POWER (W)</th><th>ORDERING PART NUMBER</th></tr>
                  <tr><td>M128-30</td><td>128</td><td>3.0</td><td>183</td><td>AC-212825002</td></tr>
                </table>""";
        assertEquals(new CpuSpecificationModel("M128-30", "Ampere Altra", "128", "3.0", "183", URL),
                ProductBriefParser.parseSpecifications(Jsoup.parse(html), "Ampere Altra", URL).getFirst());
    }

    @Test
    void missingSkuTable() {
        assertThrows(ElementNotFoundException.class,
                () -> ProductBriefParser.parseSpecifications(Jsoup.parse("<table><tr><td>Features</td></tr></table>"), "AmpereOne", URL));
    }

    @Test
    void extractAllProductBriefsLive() throws IOException, ElementNotFoundException {
        assertTrue(ProductBriefParser.extractSpecifications("Ampere Altra", "https://amperecomputing.com/briefs/ampere-altra-family-product-brief").size() >= 10);
        assertTrue(ProductBriefParser.extractSpecifications("AmpereOne", "https://amperecomputing.com/briefs/ampereone-family-product-brief").size() >= 5);
        assertTrue(ProductBriefParser.extractSpecifications("AmpereOne M", "https://amperecomputing.com/briefs/ampereone-m-product-brief").size() >= 5);
    }
}
