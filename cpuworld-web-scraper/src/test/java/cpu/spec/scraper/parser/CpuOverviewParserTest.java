package cpu.spec.scraper.parser;

import cpu.spec.scraper.exception.ElementNotFoundException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;

class CpuOverviewParserTest {

    @Test
    void testExtractSeriesLinks() throws ElementNotFoundException, IOException {
        List<String> actual = CpuOverviewParser.extractNavigationLinks();
        assertFalse(actual.isEmpty(), "actual is empty");
    }
}
