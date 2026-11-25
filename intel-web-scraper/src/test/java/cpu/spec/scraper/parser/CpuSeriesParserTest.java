package cpu.spec.scraper.parser;

import cpu.spec.scraper.exception.ElementNotFoundException;
import org.jsoup.Jsoup;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CpuSeriesParserTest {
    private static final String URL = "https://intel.com/content/www/us/en/ark/products/series/123588/intel-core-x-series-processors.html";

    @Test
    void parseSpecificationLinks() throws ElementNotFoundException {
        // structure of a series page (shortened), the same cpu can be listed twice
        String html = """
                <table id="product-table"><thead><tr><th>Product Name</th></tr></thead><tbody>
                  <tr><td data-component="arkproductlink"><a href="/content/www/us/en/products/sku/198019/x/specifications.html">i9-10900X</a></td></tr>
                  <tr><td data-component="arkproductlink"><a href="/content/www/us/en/products/sku/198017/x/specifications.html">i9-10920X</a></td></tr>
                  <tr><td data-component="arkproductlink"><a href="/content/www/us/en/products/sku/198019/x/specifications.html">i9-10900X</a></td></tr>
                  <tr><td>no link</td></tr>
                </tbody></table>""";
        assertEquals(List.of("/content/www/us/en/products/sku/198019/x/specifications.html", "/content/www/us/en/products/sku/198017/x/specifications.html"),
                CpuSeriesParser.parseSpecificationLinks(Jsoup.parse(html), URL));
    }

    @Test
    void parseChangedPageStructure() {
        assertThrows(ElementNotFoundException.class, () -> CpuSeriesParser.parseSpecificationLinks(Jsoup.parse("<table><tr><td>x</td></tr></table>"), URL));
        String withoutLinks = "<table id=\"product-table\"><tbody><tr><td>x</td></tr></tbody></table>";
        assertThrows(ElementNotFoundException.class, () -> CpuSeriesParser.parseSpecificationLinks(Jsoup.parse(withoutLinks), URL));
    }

    @Tag("live")
    @Test
    void testExtractSpecificationLinks() throws ElementNotFoundException, IOException {
        List<String> actual = CpuSeriesParser.extractSpecificationLinks(URL);
        assertTrue(actual.size() >= 14, "size >= 14");
        assertTrue(actual.getFirst().contains("/products/sku/"), "sample contains '/products/sku/'");
    }

    @Tag("live")
    @Test
    void testNonExistingUrl() {
        assertThrows(IOException.class, () -> CpuSeriesParser.extractSpecificationLinks("https://not-a-website"));
    }

    @Tag("live")
    @Test
    void testInvalidUrl() {
        assertThrows(ElementNotFoundException.class, () -> CpuSeriesParser.extractSpecificationLinks("https://google.com"));
    }
}
