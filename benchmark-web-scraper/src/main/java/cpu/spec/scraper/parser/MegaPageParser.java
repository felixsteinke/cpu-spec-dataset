package cpu.spec.scraper.parser;

import cpu.spec.scraper.CpuSpecificationModel;
import cpu.spec.scraper.exception.ElementNotFoundException;
import cpu.spec.scraper.factory.ChromeDriverFactory;
import org.jsoup.parser.Parser;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Reads all CPUs from the data table of the CPU mega page. The table loads every CPU with its specifications and
 * benchmark scores, so no detail page has to be requested.
 */
public abstract class MegaPageParser {
    private static final String ENTRY_URL = "https://www.cpubenchmark.net/CPU_mega_page.html";
    private static final String DETAIL_URL = "https://www.cpubenchmark.net/cpu.php?cpu=";
    private static final long LOAD_TIMEOUT_MS = 60_000;
    // returns all rows as json once the data table has loaded its data (via ajax), otherwise null
    private static final String ROWS_SCRIPT = """
            if (!window.jQuery || !jQuery.fn.dataTable || !jQuery.fn.dataTable.isDataTable('#cputable')) return null;
            const table = jQuery('#cputable').DataTable();
            return table.rows().count() > 0 ? JSON.stringify(table.rows().data().toArray()) : null;""";
    private static final Pattern MONTH_YEAR = Pattern.compile("^([A-Za-z]{3})\\w*\\s+(\\d{4})$");
    private static final Pattern YEAR = Pattern.compile("\\d{4}");
    private static final Pattern AMD = Pattern.compile("\\bAMD\\b");
    private static final Pattern ZEN = Pattern.compile(
            "Ryzen|EPYC|Threadripper|Athlon (Gold|Silver|PRO [23]|3\\d{3}|2\\d{2}GE|300|320)", Pattern.CASE_INSENSITIVE);
    private static final Pattern PRE_ZEN_FAMILY = Pattern.compile(
            "\\bFX-\\d|Opteron|Athlon (II )?X4|\\bA\\d{1,2}(-| PRO-)\\d{4}", Pattern.CASE_INSENSITIVE);
    private static final Map<String, Integer> MONTHS = Map.ofEntries(
            Map.entry("jan", 1), Map.entry("feb", 2), Map.entry("mar", 3), Map.entry("apr", 4), Map.entry("may", 5),
            Map.entry("jun", 6), Map.entry("jul", 7), Map.entry("aug", 8), Map.entry("sep", 9), Map.entry("oct", 10),
            Map.entry("nov", 11), Map.entry("dec", 12));
    private static final JsonMapper JSON = JsonMapper.builder().build();

    /**
     * @return all CPUs of the mega page
     * @throws ElementNotFoundException if the data table does not load any rows
     * @throws InterruptedException     if waiting for the table is interrupted
     */
    public static List<CpuSpecificationModel> extractSpecifications() throws ElementNotFoundException, InterruptedException {
        return extractSpecifications(Map.of());
    }

    /**
     * @param previousNames names of the previous dataset version (see {@link #previousNames})
     * @return all CPUs of the mega page
     * @throws ElementNotFoundException if the data table does not load any rows
     * @throws InterruptedException     if waiting for the table is interrupted
     */
    public static List<CpuSpecificationModel> extractSpecifications(Map<String, Map<String, String>> previousRecords) throws ElementNotFoundException, InterruptedException {
        WebDriver driver = ChromeDriverFactory.getDriver();
        try {
            driver.get(ENTRY_URL);
            long deadline = System.currentTimeMillis() + LOAD_TIMEOUT_MS;
            while (System.currentTimeMillis() < deadline) {
                Object json = ((JavascriptExecutor) driver).executeScript(ROWS_SCRIPT);
                if (json != null) {
                    return parseRows(json.toString(), previousRecords);
                }
                Thread.sleep(500);
            }
            throw new ElementNotFoundException(driver.getTitle(), "#cputable DataTable rows", ENTRY_URL);
        } finally {
            driver.quit();
        }
    }

    /**
     * @param records previous dataset version
     * @return previous records by cpu key ("id" or "id&amp;cpuCount=N", see {@link #cpuKey})
     */
    public static Map<String, Map<String, String>> previousRecords(List<Map<String, String>> records) {
        Map<String, Map<String, String>> previous = new HashMap<>();
        for (Map<String, String> record : records) {
            Matcher id = Pattern.compile("[?&]id=(\\d+)").matcher(record.getOrDefault("SourceUrl", ""));
            Matcher count = Pattern.compile("[?&]cpuCount=(\\d+)").matcher(record.getOrDefault("SourceUrl", ""));
            if (id.find() && !record.getOrDefault("CpuName", "").isBlank()) {
                previous.putIfAbsent(cpuKey(id.group(1), count.find() ? Integer.parseInt(count.group(1)) : 1), record);
            }
        }
        return previous;
    }

    public static List<CpuSpecificationModel> parseRows(String json) {
        return parseRows(json, Map.of());
    }

    /**
     * CPUs that exist in the previous dataset version keep their name (the id of the dataset api) and keep previous
     * values the data table does not provide (anymore), so no published value gets lost.
     *
     * @param json            data table rows (e.g. [{"id":"1908","name":"AMD A6-1450 APU","speed":"1000",...}])
     * @param previousRecords records of the previous dataset version (see {@link #previousRecords})
     * @return cpu specifications in the format of the dataset (e.g. "1.0 GHz", "8 W", "Q2 2013")
     */
    public static List<CpuSpecificationModel> parseRows(String json, Map<String, Map<String, String>> previousRecords) {
        List<CpuSpecificationModel> specifications = new ArrayList<>();
        for (JsonNode row : JSON.readTree(json)) {
            String tableName = Parser.unescapeEntities(text(row, "name"), false);
            if (tableName.isEmpty()) continue;
            String id = text(row, "id");
            int cpuCount = Math.max(1, number(row, "cpuCount"));
            Map<String, String> previous = previousRecords.getOrDefault(cpuKey(id, cpuCount), Map.of());
            String name = previous.getOrDefault("CpuName", multiCpuPrefix(cpuCount) + tableName);
            int cores = number(row, "cores");
            int logicals = number(row, "logicals");
            if (isAmdModuleCpu(tableName, quarter(text(row, "date")), logicals)) {
                // the table counts modules as cores, AMD (and the detail pages) count 2 cores per module
                cores = cores * logicals;
                logicals = 1;
            }
            int secondaryCores = number(row, "secondaryCores");
            int threads = cores * logicals + secondaryCores * number(row, "secondaryLogicals");
            String href = Parser.unescapeEntities(text(row, "href"), false);
            String sourceUrl = href.isEmpty() ? "" : DETAIL_URL + URLDecoder.decode(href, StandardCharsets.UTF_8).trim()
                    + (cpuCount > 1 ? "&cpuCount=" + cpuCount : "");
            specifications.add(new CpuSpecificationModel(
                    name,
                    orPrevious(text(row, "socket"), previous, "Socket"),
                    orPrevious(gigahertz(text(row, "speed")), previous, "ClockSpeed"),
                    orPrevious(gigahertz(text(row, "turbo")), previous, "TurboSpeed"),
                    orPrevious(cores + secondaryCores > 0 ? Integer.toString(cores + secondaryCores) : "", previous, "Cores"),
                    orPrevious(threads > 0 ? Integer.toString(threads) : "", previous, "Threads"),
                    orPrevious(text(row, "tdp").isEmpty() ? "" : text(row, "tdp") + " W", previous, "TDP"),
                    orPrevious(quarter(text(row, "date")), previous, "ReleaseDate"),
                    sourceUrl,
                    id,
                    text(row, "cat"),
                    text(row, "cpuCount"),
                    text(row, "cpumark").replace(",", ""),
                    text(row, "thread").replace(",", ""),
                    text(row, "price")));
        }
        return specifications;
    }

    /**
     * AMD Bulldozer-family CPUs (2011 - 2016, e.g. A10-7800, FX-8350, Opteron 6281) consist of modules with 2 cores
     * each, which the data table lists as 1 core with 2 logical threads. AMD only introduced SMT with Zen (2017),
     * so AMD CPUs with 2 threads per core are module based if they are no Zen product and were first tested before
     * 2017 or belong to a pre-Zen product family.
     */
    static boolean isAmdModuleCpu(String name, String releaseDate, int logicals) {
        if (logicals != 2 || !AMD.matcher(name).find() || ZEN.matcher(name).find()) {
            return false;
        }
        Matcher year = YEAR.matcher(releaseDate);
        return (year.find() && Integer.parseInt(year.group()) <= 2016) || PRE_ZEN_FAMILY.matcher(name).find();
    }

    private static String orPrevious(String value, Map<String, String> previous, String column) {
        return value.isEmpty() ? previous.getOrDefault(column, "") : value;
    }

    static String cpuKey(String id, int cpuCount) {
        return cpuCount > 1 ? id + "&cpuCount=" + cpuCount : id;
    }

    /**
     * @return name prefix of multi cpu systems like on the detail pages (e.g. "[Dual CPU] ", "[3-Way] ")
     */
    static String multiCpuPrefix(int cpuCount) {
        return switch (cpuCount) {
            case 1 -> "";
            case 2 -> "[Dual CPU] ";
            case 4 -> "[Quad CPU] ";
            default -> "[" + cpuCount + "-Way] ";
        };
    }

    /**
     * @param megahertz e.g. "3400"
     * @return e.g. "3.4 GHz" (empty for unknown or 0)
     */
    static String gigahertz(String megahertz) {
        try {
            BigDecimal mhz = new BigDecimal(megahertz);
            if (mhz.signum() <= 0) return "";
            return mhz.divide(BigDecimal.valueOf(1000), 1, RoundingMode.HALF_UP).toPlainString() + " GHz";
        } catch (NumberFormatException e) {
            return "";
        }
    }

    /**
     * @param date test date of the mega page, e.g. "May 2013"
     * @return quarter like on the detail pages, e.g. "Q2 2013" (unchanged if the format is unknown)
     */
    static String quarter(String date) {
        Matcher matcher = MONTH_YEAR.matcher(date);
        if (matcher.matches()) {
            Integer month = MONTHS.get(matcher.group(1).toLowerCase());
            if (month != null) {
                return "Q" + ((month - 1) / 3 + 1) + " " + matcher.group(2);
            }
        }
        return date;
    }

    private static String text(JsonNode row, String field) {
        String value = row.path(field).asString("").trim();
        return value.equalsIgnoreCase("NA") ? "" : value;
    }

    private static int number(JsonNode row, String field) {
        try {
            return Integer.parseInt(text(row, field));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
