package cpu.spec.scraper.parser;

import cpu.spec.scraper.CpuSpecificationModel;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MegaPageParserTest {

    @Test
    void parseRows() {
        // structure of the mega page data table rows (shortened)
        String json = """
                [{"id":"1908","name":"AMD A6-1450 APU","price":"NA","cpumark":"1,049","thread":"443","tdp":"8","date":"May 2013",
                  "socket":"FT3","cat":"Laptop","speed":"1000","turbo":"1400","cpuCount":1,"cores":"4","logicals":"1",
                  "secondaryCores":"0","secondaryLogicals":"0","href":"AMD+A6-1450+APU&amp;id=1908"},
                 {"id":"5022","name":"Intel Core i9-13900K","price":"$549.99","cpumark":"59,000","thread":"4,700","tdp":"125",
                  "date":"Sep 2022","socket":"FCLGA1700","cat":"Desktop","speed":"3000","turbo":"5800","cpuCount":1,"cores":"8",
                  "logicals":"2","secondaryCores":"16","secondaryLogicals":"1","href":"Intel+Core+i9-13900K&amp;id=5022"},
                 {"id":"7553","name":"100-000000575","price":"NA","cpumark":"15,967","tdp":"NA","date":"Nov 2026","socket":"Unknown",
                  "cat":"Unknown","speed":"0","turbo":"NA","cpuCount":1,"cores":"8","logicals":"2","href":"100-000000575&amp;id=7553"}]
                """;
        List<CpuSpecificationModel> specs = MegaPageParser.parseRows(json);
        assertEquals(3, specs.size());
        // same values as in the previous dataset (detail pages)
        assertEquals(List.of("AMD A6-1450 APU", "FT3", "1.0 GHz", "1.4 GHz", "4", "4", "8 W", "Q2 2013",
                        "https://www.cpubenchmark.net/cpu.php?cpu=AMD A6-1450 APU&id=1908", "1908", "Laptop", "1", "1049", "443", ""),
                specs.get(0).toRow());

        CpuSpecificationModel hybrid = specs.get(1);
        assertEquals("24", hybrid.cores(), "8 performance + 16 efficient cores");
        assertEquals("32", hybrid.threads(), "8 x 2 + 16 x 1 threads");
        assertEquals("Q3 2022", hybrid.releaseDate());
        assertEquals("$549.99", hybrid.price());

        CpuSpecificationModel unknown = specs.get(2);
        assertEquals("", unknown.clockSpeed(), "speed 0 is unknown");
        assertEquals("", unknown.turboSpeed());
        assertEquals("", unknown.tdp());
    }

    @Test
    void multiCpuSystemsAndPreviousNames() {
        String json = """
                [{"id":"4366","name":"AMD EPYC 7252","cpuCount":1,"cores":"8","logicals":"2","href":"AMD+EPYC+7252&amp;id=4366"},
                 {"id":"4366","name":"AMD EPYC 7252","cpuCount":2,"cores":"8","logicals":"2","href":"AMD+EPYC+7252&amp;id=4366"},
                 {"id":"9001","name":"AC8257V-WAB","cpuCount":1,"cores":"8","logicals":"1","href":"AC8257V-WAB&amp;id=9001"},
                 {"id":"9002","name":"Some CPU","cpuCount":3,"cores":"4","logicals":"1","href":"Some+CPU&amp;id=9002"}]
                """;
        // previous dataset: records identified by detail page url
        var previousRecords = MegaPageParser.previousRecords(List.of(
                Map.of("CpuName", "AC8257V/WAB", "TDP", "5 W", "Cores", "4", "SourceUrl", "https://www.cpubenchmark.net/cpu.php?cpu=AC8257V/WAB&id=9001"),
                Map.of("CpuName", "[Dual CPU] AMD EPYC 7252", "SourceUrl", "https://www.cpubenchmark.net/cpu.php?cpu=AMD EPYC 7252&id=4366&cpuCount=2")));
        List<CpuSpecificationModel> specs = MegaPageParser.parseRows(json, previousRecords);

        assertEquals("AMD EPYC 7252", specs.get(0).cpuName());
        assertEquals("https://www.cpubenchmark.net/cpu.php?cpu=AMD EPYC 7252&id=4366", specs.get(0).sourceUrl());
        assertEquals("[Dual CPU] AMD EPYC 7252", specs.get(1).cpuName());
        assertEquals("https://www.cpubenchmark.net/cpu.php?cpu=AMD EPYC 7252&id=4366&cpuCount=2", specs.get(1).sourceUrl());
        assertEquals("16", specs.get(1).threads(), "cores and threads per cpu like on the detail page");
        assertEquals("AC8257V/WAB", specs.get(2).cpuName(), "keeps the previously published name");
        assertEquals("5 W", specs.get(2).tdp(), "keeps a previous value the table does not provide");
        assertEquals("8", specs.get(2).cores(), "current values replace previous values");
        assertEquals("[3-Way] Some CPU", specs.get(3).cpuName());
    }

    @Test
    void amdModuleCpus() {
        assertTrue(MegaPageParser.isAmdModuleCpu("AMD A10-7800 APU", "Q3 2014", 2));
        assertTrue(MegaPageParser.isAmdModuleCpu("AMD FX-8350 Eight-Core", "Q4 2012", 2));
        assertTrue(MegaPageParser.isAmdModuleCpu("AMD Opteron 6281", "Q1 2019", 2), "pre-Zen family tested after 2016");
        assertFalse(MegaPageParser.isAmdModuleCpu("AMD Ryzen 7 1800X", "Q1 2017", 2));
        assertFalse(MegaPageParser.isAmdModuleCpu("AMD Custom APU 0932", "Q3 2024", 2), "Zen 2 Steam Deck APU");
        assertFalse(MegaPageParser.isAmdModuleCpu("AMD Phenom II X4 965", "Q3 2009", 1), "no SMT, no modules");
        assertFalse(MegaPageParser.isAmdModuleCpu("Intel Core i7-4770", "Q2 2013", 2));

        String json = """
                [{"id":"2297","name":"AMD A10-7800 APU","date":"Aug 2014","cpuCount":1,"cores":"2","logicals":"2","href":"x&amp;id=2297"}]""";
        CpuSpecificationModel a10 = MegaPageParser.parseRows(json).getFirst();
        assertEquals("4", a10.cores(), "2 modules with 2 cores each");
        assertEquals("4", a10.threads());
    }

    @Test
    void formats() {
        assertEquals("2.9 GHz", MegaPageParser.gigahertz("2930"));
        assertEquals("3.4 GHz", MegaPageParser.gigahertz("3400"));
        assertEquals("", MegaPageParser.gigahertz(""));
        assertEquals("Q4 2026", MegaPageParser.quarter("Nov 2026"));
        assertEquals("Q1 2021", MegaPageParser.quarter("Q1 2021"));
    }

    @Tag("live")
    @Test
    void extractSpecificationsLive() throws Exception {
        List<CpuSpecificationModel> specs = MegaPageParser.extractSpecifications();
        assertTrue(specs.size() >= 6000, "size >= 6000, was " + specs.size());
        assertTrue(specs.stream().filter(s -> !s.cores().isEmpty()).count() > specs.size() * 0.9, "cores filled for most cpus");
    }
}
