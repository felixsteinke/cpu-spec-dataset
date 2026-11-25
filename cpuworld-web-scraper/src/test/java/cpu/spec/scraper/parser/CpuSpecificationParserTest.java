package cpu.spec.scraper.parser;

import cpu.spec.scraper.CpuSpecificationModel;
import cpu.spec.scraper.exception.ElementNotFoundException;
import org.jsoup.Jsoup;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CpuSpecificationParserTest {
    private static final String URL = "https://www.cpu-world.com/CPUs/Xeon/Intel-Xeon%208272CL.html";

    @Test
    void parseCollapsedSpecificationTable() throws ElementNotFoundException {
        // structure of a specification page (shortened): collapsed sections, detail popups, nested s-spec table
        String html = """
                <div id="AB_B"><h1>Intel Xeon Platinum 8272CL</h1></div>
                <div id="GET_INFO"><table class="spec_table">
                  <tr><td colspan="2" class="spec_hd">General information</td></tr>
                  <tr><td>Family</td><td>Intel Xeon Platinum</td></tr>
                  <tr><td>Frequency <a href="#">?</a></td><td>2.6 GHz / 2600 MHz</td></tr>
                  <tr><td>Maximum turbo frequency</td><td>3.7 GHz / 3700 MHz [1]</td></tr>
                  <tr><td>Part number</td><td></td></tr>
                  <tr><td colspan="2"><div><table><tr><td>Part number</td><td>Production processors</td></tr><tr><td></td><td>SRF89</td></tr></table></div></td></tr>
                  <tr style="display: none"><td>Microarchitecture</td><td>Cascade Lake<div class="spec_detd">Details about Cascade Lake</div></td></tr>
                  <tr style="display: none"><td>Processor codename</td><td>Cascade Lake-SP</td></tr>
                  <tr style="display: none"><td>The number of CPU cores</td><td>26</td></tr>
                  <tr style="display: none"><td>The number of threads</td><td>52</td></tr>
                  <tr style="display: none"><td>Thermal Design Power</td><td>195 Watt</td></tr>
                  <tr style="display: none"><td>Low power frequency</td><td></td></tr>
                </table></div>
                <script>var x = 1;</script>""";
        CpuSpecificationModel spec = CpuSpecificationParser.parseSpecification(Jsoup.parse(html, URL), URL);
        assertEquals("Intel-Xeon%208272CL", spec.id);
        assertEquals("Intel Xeon Platinum 8272CL", spec.cpuName);
        assertEquals(URL, spec.sourceUrl);
        assertEquals("2.6 GHz / 2600 MHz", spec.dataValues.get("Frequency"), "help link '?' removed from key");
        assertEquals("3.7 GHz / 3700 MHz", spec.dataValues.get("Maximum turbo frequency"), "footnote removed");
        assertEquals("Cascade Lake", spec.dataValues.get("Microarchitecture"), "hidden row read, popup removed");
        assertEquals("Cascade Lake-SP", spec.dataValues.get("Processor core"), "renamed label keeps the dataset column");
        assertEquals("26", spec.dataValues.get("The number of CPU cores"));
        assertEquals("52", spec.dataValues.get("The number of threads"));
        assertEquals("195 Watt", spec.dataValues.get("Thermal Design Power"));
        assertTrue(spec.dataValues.containsKey("Low power frequency") && spec.dataValues.get("Low power frequency") == null);
        assertFalse(spec.dataValues.containsKey("Part number"), "ignored key");
    }

    @Test
    void missingSpecificationTable() {
        assertThrows(ElementNotFoundException.class,
                () -> CpuSpecificationParser.parseSpecification(Jsoup.parse("<div id=\"AB_B\"><h1>x</h1></div>"), URL));
    }

    @Tag("live")
    @Test
    void testExtractSpecificationSample() throws Exception {
        CpuSpecificationModel actual = CpuSpecificationParser.extractSpecification("https://www.cpu-world.com/CPUs/Xeon/Intel-Xeon 8272CL.html");
        assertEquals("Intel-Xeon 8272CL", actual.id);
        assertEquals("Intel Xeon Platinum 8272CL", actual.cpuName);
        assertEquals("https://www.cpu-world.com/CPUs/Xeon/Intel-Xeon 8272CL.html", actual.sourceUrl);

        assertEquals("26", actual.dataValues.get("The number of CPU cores"));
        assertEquals("52", actual.dataValues.get("The number of threads"));
        assertEquals("Cascade Lake", actual.dataValues.get("Microarchitecture"));
    }
}
