package cpu.spec.scraper.parser;

import cpu.spec.scraper.exception.ElementNotFoundException;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CpuSeriesParserTest {

    @Tag("live")
    @Test
    void testExtractSpecificationLinks() throws ElementNotFoundException, IOException {
        List<String> actual = CpuSeriesParser.extractNavigationLinks("https://www.cpu-world.com/CPUs/Xeon/TYPE-Xeon Platinum.html");
        assertFalse(actual.isEmpty(), "actual is empty");
    }

    @Tag("live")
    @Test
    void testNonExistingUrl() {
        assertThrows(IOException.class, () -> CpuSeriesParser.extractNavigationLinks("https://not-a-website"));
    }

    @Tag("live")
    @Test
    void testInvalidUrl() {
        assertThrows(ElementNotFoundException.class, () -> CpuSeriesParser.extractNavigationLinks("https://google.com"));
    }
}
