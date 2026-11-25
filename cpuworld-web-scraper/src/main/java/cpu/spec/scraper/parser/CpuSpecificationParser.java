package cpu.spec.scraper.parser;

import cpu.spec.scraper.CpuSpecificationModel;
import cpu.spec.scraper.exception.ElementNotFoundException;
import cpu.spec.scraper.factory.ChromeDriverFactory;
import cpu.spec.scraper.validator.JsoupValidator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;

import java.util.Map;

public abstract class CpuSpecificationParser {
    /**
     * labels renamed on the website -> column name of the dataset (keeps the dataset columns stable)
     */
    private static final Map<String, String> KEY_ALIASES = Map.of("Processor codename", "Processor core");

    /**
     * @param url <a href="https://www.cpu-world.com/CPUs/Xeon/Intel-Xeon%208272CL.html">Cpu World Specification Page</a>
     * @return cpu specification model
     * @throws Exception if page cannot be retrieved or elements cannot be retrieved
     */
    public static CpuSpecificationModel extractSpecification(String url) throws Exception {
        WebDriver driver = ChromeDriverFactory.getDriver();
        try {
            driver.get(url);
            // Wait for the dynamic content to load
            Thread.sleep(250);
            return parseSpecification(Jsoup.parse(driver.getPageSource(), url), url);
        } finally {
            driver.quit();
        }
    }

    /**
     * The specification table collapses most of its sections, so the values are read from the page source
     * (independent of their visibility) instead of the rendered text.
     *
     * @param page specification page
     * @param url  source url of the page
     * @return cpu specification model
     * @throws ElementNotFoundException if title or specification table cannot be found
     */
    public static CpuSpecificationModel parseSpecification(Document page, String url) throws ElementNotFoundException {
        JsoupValidator validator = new JsoupValidator(url);
        // remove detail popups and scripts, they are not part of the values
        page.select(".spec_detd, script, style").remove();

        CpuSpecificationModel specification = new CpuSpecificationModel();
        specification.id = selectId(url);
        specification.sourceUrl = url;
        specification.cpuName = validator.selectFirst(page, "div#AB_B h1").text().trim();

        Element specTable = validator.selectFirst(page, "div#GET_INFO table.spec_table");
        for (Element tableRow : specTable.select("tr")) {
            Elements cells = tableRow.select("> td");
            if (cells.size() < 2) {
                continue;
            }
            String dataKey = cleanValue(cells.get(0).text());
            dataKey = KEY_ALIASES.getOrDefault(dataKey, dataKey);
            if (dataKey.isBlank() || isKeyIgnored(dataKey) || specification.dataValues.containsKey(dataKey)) {
                continue;
            }
            String dataValue = cleanValue(cells.get(1).text());
            specification.dataValues.put(dataKey, dataValue.isBlank() ? null : dataValue);
        }
        return specification;
    }

    private static String selectId(String url) {
        String[] split = url.split("/");
        if (split.length > 0) {
            return split[split.length - 1].trim().replaceAll(".html", "");
        } else {
            return null;
        }
    }

    private static String cleanValue(String value) {
        if (value == null) {
            return null;
        }
        return value.replaceAll("\n", " ")
                .replaceAll("\\?", "")
                .replaceAll("\\[\\d]", "")
                .trim();
    }

    private static boolean isKeyIgnored(String key) {
        var normedKey = key.toLowerCase();
        return normedKey.contains("part number")
                || normedKey.contains("memory controller")
                || normedKey.contains("other peripherals")
                || normedKey.contains("extensions")
                || normedKey.contains("package")
                || normedKey.contains("none")
                || normedKey.contains("unknown")
                || key.startsWith("BX")
                || key.startsWith("CD")
                || key.startsWith("PK");
    }
}
