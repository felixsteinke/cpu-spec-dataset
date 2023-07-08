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
     * @param url <a href="">Intel Processor Series Page</a>
     * @return cpu links for sub routing
     * @throws IOException              if page cannot be retrieved
     * @throws ElementNotFoundException if element cannot be retrieved
     */
    public static List<String> extractNavigationLinks(String url) throws IOException, ElementNotFoundException {
        Document page = JsoupFactory.getConnection(url).get();
        JsoupValidator validator = new JsoupValidator(url);

        Elements cpuNames = validator.select(page, "div.cpu_name");

        List<String> specificationLinks = new ArrayList<>();
        for (Element row : cpuNames) {
            Element aSpec = row.selectFirst("a");
            if (aSpec == null) {
                continue;
            }
            String link = aSpec.attr("href");
            if (link.isBlank()) {
                continue;
            }
            specificationLinks.add(link);
        }
        return specificationLinks;
    }
}
