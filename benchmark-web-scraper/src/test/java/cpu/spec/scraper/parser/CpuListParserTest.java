package cpu.spec.scraper.parser;

import cpu.spec.scraper.exception.ElementNotFoundException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CpuListParserTest {

    @Test
    void testExtractSpecificationLinks() throws ElementNotFoundException, IOException {
        List<String> actual = CpuListParser.extractSpecificationLinks("cpu_mega_page.html");
        assertTrue(actual.size() >= 4200, "size >= 14");
        assertTrue(actual.get(0).contains("cpu.php?cpu="), "sample contains 'cpu.php?cpu='");
    }

    @Test
    void testNonExistingUrl() {
        assertThrows(IOException.class, () -> CpuListParser.extractSpecificationLinks("https://not-a-website"));
    }

    @Test
    void testInvalidUrl() {
        assertThrows(IOException.class, () -> CpuListParser.extractSpecificationLinks("https://google.com"));
    }
}
