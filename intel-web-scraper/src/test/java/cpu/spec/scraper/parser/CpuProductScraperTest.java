package cpu.spec.scraper.parser;

import cpu.spec.scraper.exception.ElementNotFoundException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class CpuProductScraperTest {

    @Test
    void testExtractSeriesLinks() throws ElementNotFoundException, IOException {
        List<String> actual = CpuProductScraper.extractSeriesLinks();
        assertTrue(actual.size() >= 114, "size >= 114");
        assertTrue(actual.get(0).contains("/content/www/us/en/ark/products/series"), "sample contains '/content/www/us/en/ark/products/series'");
    }
}
