package cpu.spec.scraper.parser;

import cpu.spec.scraper.exception.ElementNotFoundException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CpuSeriesParserTest {

    @Test
    void testExtractSpecificationLinks() throws ElementNotFoundException, IOException {
        List<String> actual = CpuSeriesParser.extractNavigationLinks("https://www.cpu-world.com/CPUs/Xeon/TYPE-Xeon Platinum.html");
        assertFalse(actual.isEmpty(), "actual is empty");
    }

    @Test
    void testNonExistingUrl() {
        assertThrows(IOException.class, () -> CpuSeriesParser.extractNavigationLinks("https://not-a-website"));
    }

    @Test
    void testInvalidUrl() {
        assertThrows(ElementNotFoundException.class, () -> CpuSeriesParser.extractNavigationLinks("https://google.com"));
    }
}
