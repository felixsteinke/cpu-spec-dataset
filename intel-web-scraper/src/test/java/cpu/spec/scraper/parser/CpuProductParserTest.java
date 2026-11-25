package cpu.spec.scraper.parser;

import cpu.spec.scraper.exception.ElementNotFoundException;
import org.jsoup.Jsoup;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CpuProductParserTest {

    @Test
    void parseSeriesLinks() throws ElementNotFoundException {
        // structure of the product overview (shortened): processor generations with their series panels
        String html = """
                <div data-parent-panel-key="Processors">
                  <div><div data-panel-key="CoreUltra">Intel® Core™ Ultra Processors</div></div>
                  <div><div data-panel-key="Xeon">Intel® Xeon® Processors</div></div>
                </div>
                <div data-parent-panel-key="CoreUltra"><div><span><a href="/content/www/us/en/ark/products/series/241071/x.html">Series 2</a></span></div></div>
                <div data-parent-panel-key="Xeon">
                  <a href="/content/www/us/en/ark/products/series/595/x.html">Xeon 6</a>
                  <a href="/content/www/us/en/ark/products/series/241071/x.html">duplicate</a>
                </div>""";
        assertEquals(List.of("/content/www/us/en/ark/products/series/241071/x.html", "/content/www/us/en/ark/products/series/595/x.html"),
                CpuProductParser.parseSeriesLinks(Jsoup.parse(html)));
    }

    @Test
    void parseChangedPageStructure() {
        assertThrows(ElementNotFoundException.class, () -> CpuProductParser.parseSeriesLinks(Jsoup.parse("<div>Processors</div>")));
        String withoutSeries = "<div data-parent-panel-key=\"Processors\"><div data-panel-key=\"Xeon\">Xeon</div></div>";
        assertThrows(ElementNotFoundException.class, () -> CpuProductParser.parseSeriesLinks(Jsoup.parse(withoutSeries)));
    }

    @Tag("live")
    @Test
    void testExtractSeriesLinks() throws ElementNotFoundException, IOException {
        List<String> actual = CpuProductParser.extractSeriesLinks();
        assertTrue(actual.size() >= 114, "size >= 114");
        assertTrue(actual.getFirst().contains("/products/series/"), "sample contains '/products/series/'");
    }
}
