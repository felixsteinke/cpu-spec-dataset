package cpu.spec.scraper.parser;

import cpu.spec.scraper.exception.ElementNotFoundException;
import cpu.spec.scraper.parser.SpecificationTableParser.Table;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SpecificationTableParserTest {
    // structure of the "data-json" attribute (shortened)
    private static final String JSON = """
            {"items": [{
              "productPages": {"en": "https://www.amd.com/en/products/processors/desktops/ryzen/9000-series/amd-ryzen-9-9950x.html"},
              "elementsOrder": ["name", "series", "numOfCpuCores", "maxBoostClock", "defaultTdp", "osSupport", "pciExpressVersion"],
              "elements": {
                "name": {"title": "Name", "value": "AMD Ryzen&trade; 9 9950X", "formatValue": "AMD Ryzen&trade; 9 9950X"},
                "series": {"title": "Series", "value": ["Ryzen 9000 Series"], "formatValue": ["Ryzen 9000 Series"], "multiValue": true},
                "numOfCpuCores": {"title": "# of CPU Cores", "value": "16", "formatValue": "16"},
                "maxBoostClock": {"title": "Max. Boost Clock", "value": "5700.0", "formatValue": "5.7 GHz", "prefix": "Up to "},
                "defaultTdp": {"title": "Default TDP", "value": ["170"], "suffix": "W", "multiValue": true},
                "osSupport": {"title": "*OS Support", "formatValue": ["Windows 11 - 64-Bit Edition", "RHEL x86 64-Bit"], "multiValue": true},
                "pciExpressVersion": {"title": "PCI Express&reg; Version", "formatValue": ["PCIe&reg; 5.0"], "multiValue": true}
              }
            }, {
              "productPages": {"en": "https://www.amd.com/en/products/processors/server/epyc/4th-generation-9004-and-8004-series/amd-epyc-9654.html"},
              "elements": {
                "name": {"title": "Name", "formatValue": "AMD EPYC&trade; 9654"},
                "maxBoostClock": {"title": "Max. Boost Clock", "prefix": "Up to "},
                "socketCount": {"title": "Socket Count", "formatValue": ["1P / 2P"], "multiValue": true}
              }
            }]}
            """;

    @Test
    void parseTable() {
        Table table = SpecificationTableParser.parseTable(JSON);
        assertEquals(List.of("Name", "Series", "# of CPU Cores", "Max. Boost Clock", "Default TDP", "*OS Support",
                "PCI Express® Version", "Socket Count", "SourceUrl"), table.header());
        assertEquals(2, table.rows().size());

        Map<String, String> ryzen = table.rows().get(0);
        assertEquals("AMD Ryzen™ 9 9950X", ryzen.get("Name"));
        assertEquals("Ryzen 9000 Series", ryzen.get("Series"));
        assertEquals("16", ryzen.get("# of CPU Cores"));
        assertEquals("Up to 5.7 GHz", ryzen.get("Max. Boost Clock"));
        assertEquals("170W", ryzen.get("Default TDP"), "falls back to value and appends the suffix");
        assertEquals("Windows 11 - 64-Bit Edition , RHEL x86 64-Bit", ryzen.get("*OS Support"));
        assertEquals("PCIe® 5.0", ryzen.get("PCI Express® Version"));
        assertEquals("https://www.amd.com/en/products/processors/desktops/ryzen/9000-series/amd-ryzen-9-9950x.html", ryzen.get("SourceUrl"));

        Map<String, String> epyc = table.rows().get(1);
        assertEquals("AMD EPYC™ 9654", epyc.get("Name"));
        assertEquals("", epyc.get("Max. Boost Clock"), "prefix without value is not written");
        assertEquals("1P / 2P", epyc.get("Socket Count"));
    }

    @Tag("live")
    @Test
    void extractTableLive() throws IOException, ElementNotFoundException {
        Table table = SpecificationTableParser.extractTable("https://www.amd.com/en/products/specifications/processors.html");
        assertTrue(table.rows().size() >= 700, "rows >= 700, was " + table.rows().size());
        assertTrue(table.header().containsAll(List.of("Name", "# of CPU Cores", "# of Threads", "Base Clock", "Max. Boost Clock", "Default TDP", "Launch Date")));
    }

    @Tag("live")
    @Test
    void invalidPage() {
        assertThrows(ElementNotFoundException.class, () -> SpecificationTableParser.extractTable("https://www.amd.com/en.html"));
    }
}
