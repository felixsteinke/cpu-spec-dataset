package cpu.spec.scraper.parser;

import cpu.spec.scraper.CpuSpecificationModel;
import cpu.spec.scraper.exception.ElementNotFoundException;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CpuSpecificationParserTest {

    @BeforeAll
    static void setup() {
        WebDriverManager.chromedriver().setup();
    }

    @Test
    void testExtractSpecificationSample() throws Exception {
        CpuSpecificationModel actual = CpuSpecificationParser.extractSpecification("https://www.cpu-world.com/CPUs/Xeon/Intel-Xeon 8272CL.html");
        assertEquals("Intel-Xeon 8272CL", actual.id);
        assertEquals("Intel Xeon Platinum 8272CL", actual.cpuName);
        assertEquals("https://www.cpu-world.com/CPUs/Xeon/Intel-Xeon 8272CL.html", actual.sourceUrl);

        assertEquals("26", actual.dataValues.get("The number of CPU cores"));
        assertEquals("52", actual.dataValues.get("The number of threads"));
    }

    @Test
    void testNonExistingUrl() {
        assertThrows(IOException.class, () -> CpuSpecificationParser.extractSpecification("https://not-a-website"));
    }

    @Test
    void testInvalidUrl() {
        assertThrows(ElementNotFoundException.class, () -> CpuSpecificationParser.extractSpecification("https://google.com"));
    }
}
