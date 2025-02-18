package cpu.spec.scraper.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cpu.spec.scraper.exception.ElementNotFoundException;
import cpu.spec.scraper.factory.JsoupFactory;

public abstract class CpuSeriesParser {
    /**
     * @param url <a href="https://ark.intel.com/content/www/us/en/ark/products/series/230485/13th-generation-intel-core-i9-processors.html">Intel Processor Series Page</a>
     * @return cpu links for sub routing
     * @throws IOException              if page cannot be retrieved
     * @throws ElementNotFoundException if element cannot be retrieved
     */
    public static List<String> extractSpecificationLinks(String url) throws IOException, ElementNotFoundException {
        Document page = JsoupFactory.getConnection(url).get();

        // xPath: table with id "product-table" -> table body -> table rows
        String xPathQuery = ".//table[@id='product-table']//tbody/tr";
        Elements tableRows = page.selectXpath(xPathQuery);

        List<String> specificationLinks = new ArrayList<>();
        for (Element row : tableRows) {

            // xpath: table data with data-component "arkproductlink" -> "a" elements
            xPathQuery = ".//td[@data-component='arkproductlink']//a";
            
            Element linkElement = row.selectXpath(xPathQuery).first();
            if (linkElement != null) {
                specificationLinks.add(linkElement.attr("href"));
            }
        }
        return specificationLinks;
    }
}
