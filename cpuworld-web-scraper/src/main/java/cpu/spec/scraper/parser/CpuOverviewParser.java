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

public abstract class CpuOverviewParser {
    /**
     * @param url <a href="https://www.cpu-world.com/CPUs/CPU.html">CpuWorld Main Page</a>
     * @return series links for sub routing
     * @throws IOException              if page cannot be retrieved
     * @throws ElementNotFoundException if element cannot be retrieved
     */
    public static List<String> extractNavigationLinks(String url) throws IOException, ElementNotFoundException {
        Document page = JsoupFactory.getConnection(url).get();
        JsoupValidator validator = new JsoupValidator(url);

        Elements manufacturerTables = validator.select(page, "table.sh_table");

        List<String> links = new ArrayList<>();
        for (Element table : manufacturerTables) {
            Elements tableRows = table.select("tr");
            for (Element tr : tableRows) {
                Element headColumn = tr.selectFirst("td");
                if (headColumn == null) {
                    continue;
                }
                Element a = headColumn.selectFirst("a");
                if (a == null) {
                    continue;
                }
                String href = a.attr("href");
                if (href.isBlank()) {
                    continue;
                }
                links.add(href);
            }
        }
        return links;
    }
}
