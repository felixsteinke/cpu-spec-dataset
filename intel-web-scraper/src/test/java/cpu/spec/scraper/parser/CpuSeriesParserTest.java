package cpu.spec.scraper.parser;

import cpu.spec.scraper.exception.ElementNotFoundException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CpuSeriesParserTest {

    @Test
    void testExtractSpecificationLinks() throws ElementNotFoundException, IOException {
        List<String> actual = CpuSeriesParser.extractSpecificationLinks("https://ark.intel.com/content/www/us/en/ark/products/series/123588/intel-core-x-series-processors.html");
        assertTrue(actual.size() >= 14, "size >= 14");
        assertTrue(actual.get(0).contains("/content/www/us/en/ark/products"), "sample contains '/content/www/us/en/ark/products'");
    }

    @Test
    void testNonExistingUrl() {
        assertThrows(IOException.class, () -> CpuSeriesParser.extractSpecificationLinks("https://not-a-website"));
    }

    @Test
    void testInvalidUrl() {
        assertThrows(ElementNotFoundException.class, () -> CpuSeriesParser.extractSpecificationLinks("https://google.com"));
    }
}
