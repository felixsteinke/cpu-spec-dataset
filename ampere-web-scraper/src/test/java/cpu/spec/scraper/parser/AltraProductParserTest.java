package cpu.spec.scraper.parser;

import cpu.spec.scraper.exception.ElementNotFoundException;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AltraProductParserTest {

    @Test
    void testExtractSpecificationSample() throws ElementNotFoundException, IOException {
        var specifications = AltraProductParser.extractSpecification();
        assertNotNull(specifications);
        assertFalse(specifications.isEmpty(), "specifications is empty");
    }
}
