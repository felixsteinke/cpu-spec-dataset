package cpu.spec.scraper.parser;

import cpu.spec.scraper.exception.ElementNotFoundException;
import cpu.spec.scraper.factory.ChromeDriverFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static cpu.spec.scraper.validator.SeleniumValidator.validate;

public abstract class CpuListParser {
    private static final String ENTRY_URL = "https://www.cpubenchmark.net/CPU_mega_page.html";

    /**
     * @return cpu links for sub routing (url decoded)
     * @throws ElementNotFoundException if element cannot be retrieved
     */
    public static List<String> extractSpecificationLinks() throws Exception {
        WebDriver driver = ChromeDriverFactory.getDriver();
        List<String> specificationLinks = new ArrayList<>();

        try {
            driver.get(ENTRY_URL);
            Thread.sleep(250);
            showAllEntries(driver);
            Thread.sleep(250);

            WebElement tableBody = driver.findElement(By.cssSelector("#cputable > tbody"));
            validate(tableBody, "Page", "#cputable > tbody");

            List<WebElement> tableRows = tableBody.findElements(By.cssSelector("tr"));
            validate(tableRows, "#cputable > tbody", "tr");


            for (WebElement row : tableRows) {
                WebElement aSpec = row.findElement(By.tagName("a"));
                if (aSpec == null) {
                    continue;
                }
                specificationLinks.add(URLDecoder.decode(aSpec.getAttribute("href").replaceAll("cpu_lookup.php", "cpu.php"), StandardCharsets.UTF_8));
            }

        } catch (Exception exception) {
            driver.quit();
            throw exception;
        }
        driver.quit();
        return specificationLinks;
    }

    private static void showAllEntries(WebDriver driver) throws ElementNotFoundException {
        WebElement selectElement = driver.findElement(By.name("cputable_length"));
        validate(selectElement, "Page", "cputable_length");
        Select select = new Select(selectElement);
        select.selectByVisibleText("All");
    }
}
