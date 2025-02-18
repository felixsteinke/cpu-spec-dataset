package cpu.spec.scraper.parser;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cpu.spec.scraper.CpuSpecificationModel;
import cpu.spec.scraper.exception.ElementNotFoundException;
import cpu.spec.scraper.factory.JsoupFactory;

public abstract class CpuSpecificationParser {
    /**
     * @param url <a href="https://ark.intel.com/content/www/us/en/ark/products/226449/intel-pentium-gold-processor-8500-8m-cache-up-to-4-40-ghz.html">Intel Processor Specification Page</a>
     * @return cpu specification model
     * @throws IOException              if page cannot be retrieved
     * @throws ElementNotFoundException if element cannot be retrieved
     */
    public static CpuSpecificationModel extractSpecification(String url) throws IOException, ElementNotFoundException {
        Document page = JsoupFactory.getConnection(url).get();
        CpuSpecificationModel specification = new CpuSpecificationModel();
        
        // Select title element
        // xPath: divs with class='product-details' -> flexible element with itemprop=name
        String xPathQuery = ".//div[@class='product-details']//*[@itemprop='name']"; 
        Element titleElement = page.selectXpath(xPathQuery).first();

        specification.id = selectId(url);
        specification.cpuName = titleElement.text();
        specification.sourceUrl = url;
        
        // Extract specifications
        // xPath: divs with class "products processors" -> "a" elements with hrefs containing "processor" -> hrefs
        xPathQuery = ".//div[contains(@id, 'spec')]//div[contains(@class, 'tech-section')]";
        Elements specElements = page.selectXpath(xPathQuery);
        for (Element specElement : specElements) {
            xPathQuery = ".//div[contains(@class, 'tech-label')]";
            Element dataKeyElement = specElement.selectXpath(xPathQuery).first();
            xPathQuery = ".//div[contains(@class, 'tech-data')]";
            Element dataValue = specElement.selectXpath(xPathQuery).first();

            if (dataKeyElement != null && dataValue != null && !isKeyIgnored(dataKeyElement.text())) {
                specification.dataValues.put(dataKeyElement.text().trim(), dataValue.text().trim());
            }
        }
        return specification;
    }

    private static String selectId(String url) {
        try {
            return new URI(url).getPath().trim().split("/")[7];
        } catch (URISyntaxException e) {
            return e.getClass().getSimpleName();
        }
    }

    private static boolean isKeyIgnored(String key) {
        return key.equalsIgnoreCase("DatasheetUrl");
    }
}
