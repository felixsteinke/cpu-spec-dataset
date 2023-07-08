package cpu.spec.scraper.parser;

import cpu.spec.scraper.exception.ElementNotFoundException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CpuOverviewParserTest {

    @Test
    void testExtractSeriesLinks() throws ElementNotFoundException, IOException {
        List<String> actual = CpuOverviewParser.extractNavigationLinks("https://www.cpu-world.com/CPUs/CPU.html");
        assertFalse(actual.isEmpty(), "actual is empty");
    }

    @Test
    void testNonExistingUrl() {
        assertThrows(IOException.class, () -> CpuOverviewParser.extractNavigationLinks("https://not-a-website"));
    }

    @Test
    void testInvalidUrl() {
        assertThrows(ElementNotFoundException.class, () -> CpuOverviewParser.extractNavigationLinks("https://google.com"));
    }
}
