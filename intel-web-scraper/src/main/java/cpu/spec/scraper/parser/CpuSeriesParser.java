package cpu.spec.scraper.parser;

import cpu.spec.scraper.exception.ElementNotFoundException;
import cpu.spec.scraper.factory.JsoupFactory;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public abstract class CpuSeriesParser {
    // xPath: table with id "product-table" -> table body -> table rows
    private static final String ROW_QUERY = ".//table[@id='product-table']//tbody/tr";
    // xPath: table data with data-component "arkproductlink" -> "a" elements
    private static final String LINK_QUERY = ".//td[@data-component='arkproductlink']//a";

    /**
     * @param url <a href="https://www.intel.com/content/www/us/en/ark/products/series/123588/intel-core-x-series-processors.html">Intel Processor Series Page</a>
     * @return cpu links for sub routing
     * @throws IOException              if page cannot be retrieved
     * @throws ElementNotFoundException if element cannot be retrieved
     */
    public static List<String> extractSpecificationLinks(String url) throws IOException, ElementNotFoundException {
        Document page = JsoupFactory.getCrawlerConnection(url).get();
        return parseSpecificationLinks(page, url);
    }

    /**
     * @return distinct specification links (e.g. /content/www/us/en/products/sku/201900/.../specifications.html)
     * @throws ElementNotFoundException if the product table or its links cannot be found
     */
    public static List<String> parseSpecificationLinks(Document page, String url) throws ElementNotFoundException {
        Elements tableRows = page.selectXpath(ROW_QUERY);
        if (tableRows.isEmpty()) {
            throw new ElementNotFoundException("document", ROW_QUERY, url);
        }
        Set<String> specificationLinks = new LinkedHashSet<>();
        for (Element row : tableRows) {
            Element linkElement = row.selectXpath(LINK_QUERY).first();
            if (linkElement != null && !linkElement.attr("href").isBlank()) {
                specificationLinks.add(linkElement.attr("href"));
            }
        }
        if (specificationLinks.isEmpty()) {
            throw new ElementNotFoundException("tr", LINK_QUERY, url);
        }
        return new ArrayList<>(specificationLinks);
    }
}
