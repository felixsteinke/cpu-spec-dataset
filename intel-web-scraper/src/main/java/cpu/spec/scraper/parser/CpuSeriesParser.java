package cpu.spec.scraper.parser;

import cpu.spec.scraper.exception.ElementNotFoundException;
import cpu.spec.scraper.factory.JsoupFactory;
import cpu.spec.scraper.validator.JsoupValidator;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class CpuSeriesParser {
    /**
     * @param url <a href="https://ark.intel.com/content/www/us/en/ark/products/series/230485/13th-generation-intel-core-i9-processors.html">Intel Processor Series Page</a>
     * @return cpu links for sub routing
     * @throws IOException              if page cannot be retrieved
     * @throws ElementNotFoundException if element cannot be retrieved
     */
    public static List<String> extractSpecificationLinks(String url) throws IOException, ElementNotFoundException {
        Document page = JsoupFactory.getConnection(url).get();
        JsoupValidator validator = new JsoupValidator(url);

        Element tableBody = validator.selectFirst(page, "tbody");

        Elements tableRows = validator.select(tableBody, "tr");

        List<String> specificationLinks = new ArrayList<>();
        for (Element row : tableRows) {
            Element firstColumn = row.selectFirst("td");
            if (firstColumn == null) {
                continue;
            }
            Element aSpec = row.selectFirst("a");
            if (aSpec == null) {
                continue;
            }
            specificationLinks.add(aSpec.attr("href"));
        }
        return specificationLinks;
    }
}
