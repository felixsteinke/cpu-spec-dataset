package cpu.spec.scraper.parser;

import cpu.spec.scraper.exception.ElementNotFoundException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static cpu.spec.scraper.validator.JsoupValidator.validate;

public abstract class CpuSeriesParser {
    /**
     * @param url <a href="https://ark.intel.com/content/www/us/en/ark/products/series/230485/13th-generation-intel-core-i9-processors.html">Intel Processor Series Page</a>
     * @return cpu links for sub routing
     * @throws IOException              if page cannot be retrieved
     * @throws ElementNotFoundException if element cannot be retrieved
     */
    public static List<String> extractSpecificationLinks(String url) throws IOException, ElementNotFoundException {
        Document page = Jsoup.connect(url).get();

        Element tableBody = page.selectFirst("tbody");
        validate(tableBody, "Page", "tbody");

        Elements tableRows = tableBody.select("tr");
        validate(tableRows, "tbody", "tr");

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
