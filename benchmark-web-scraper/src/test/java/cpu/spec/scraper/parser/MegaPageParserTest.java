package cpu.spec.scraper.parser;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class MegaPageParserTest {

    @Test
    void testExtractSpecificationLinks() throws Exception {
        List<String> actual = MegaPageParser.extractSpecificationLinks();
        assertTrue(actual.size() >= 4200, "size >= 4200");
        assertTrue(actual.get(0).contains("cpu.php?cpu="), "sample contains 'cpu.php?cpu='");
    }
}
