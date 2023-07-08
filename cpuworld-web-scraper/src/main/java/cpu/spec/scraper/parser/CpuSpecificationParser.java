package cpu.spec.scraper.parser;

import cpu.spec.scraper.CpuSpecificationModel;
import cpu.spec.scraper.factory.ChromeDriverFactory;
import cpu.spec.scraper.validator.SeleniumValidator;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public abstract class CpuSpecificationParser {

    /**
     * @param url <a href="https://www.cpu-world.com/CPUs/Xeon/Intel-Xeon%208272CL.html">Cpu World Specification Page</a>
     * @return cpu specification model
     * @throws Exception if page cannot be retrieved or elements cannot be retrieved
     */
    public static CpuSpecificationModel extractSpecification(String url) throws Exception {
        WebDriver driver = ChromeDriverFactory.getDriver();
        SeleniumValidator validator = new SeleniumValidator(url);
        CpuSpecificationModel specification = new CpuSpecificationModel();

        try {
            driver.get(url);
            specification.id = selectId(url);
            specification.sourceUrl = url;

            // Wait for the dynamic content to load (adjust the wait time according to your needs)
            Thread.sleep(250);

            // Extract the CPU name from the page
            WebElement mainDiv = driver.findElement(By.cssSelector("div#AB_B"));
            if (mainDiv != null) {
                WebElement titleElement = mainDiv.findElement(By.tagName("h1"));
                if (titleElement != null) {
                    specification.cpuName = titleElement.getText();
                }
            }
            // Extract the specification table
            WebElement infoDiv = validator.findElement(driver, By.cssSelector("div#GET_INFO"));

            WebElement specTable = validator.findElement(infoDiv, By.cssSelector("table.spec_table"));

            // Iterate over each row of the table
            List<WebElement> tableRows = specTable.findElements(By.tagName("tr"));
            for (WebElement tableRow : tableRows) {
                List<WebElement> tdList = tableRow.findElements(By.tagName("td"));
                if (tdList.size() < 2) {
                    continue;
                }
                String dataKey = cleanValue(tdList.get(0).getText());
                if (dataKey.isBlank() || isKeyIgnored(dataKey)) {
                    continue;
                }
                String dataValue = cleanValue(tdList.get(1).getText());
                if (dataValue.isBlank()) {
                    specification.dataValues.put(dataKey, null);
                    continue;
                }
                specification.dataValues.put(dataKey, dataValue);
            }
        } catch (Exception exception) {
            driver.quit();
            throw exception;
        }
        driver.quit();
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
                || normedKey.contains("unknown");
    }
}
