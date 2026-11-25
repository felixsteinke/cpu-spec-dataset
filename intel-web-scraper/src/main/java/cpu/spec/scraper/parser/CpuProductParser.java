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

public abstract class CpuProductParser {
    private static final String ENTRY_URL = "https://www.intel.com/content/www/us/en/ark.html";
    // xPath: divs with data-parent-panel-key='Processors' -> divs with data-panel-key (generations)
    private static final String GENERATION_QUERY = ".//div[@data-parent-panel-key='Processors']//div[@data-panel-key]";
    // xPath: "a" children of divs with the generation label
    private static final String SERIES_QUERY = ".//div[@data-parent-panel-key='%s']//a";

    /**
     * @return series links for sub routing
     * @throws IOException              if page cannot be retrieved
     * @throws ElementNotFoundException if element cannot be retrieved
     */
    public static List<String> extractSeriesLinks() throws IOException, ElementNotFoundException {
        Document page = JsoupFactory.getCrawlerConnection(ENTRY_URL).get();
        return parseSeriesLinks(page);
    }

    /**
     * @param page <a href="https://www.intel.com/content/www/us/en/ark.html">Intel Product Specifications</a>
     * @return distinct series links (e.g. /content/www/us/en/ark/products/series/123588/intel-core-x-series-processors.html)
     * @throws ElementNotFoundException if no processor generations or series links can be found
     */
    public static List<String> parseSeriesLinks(Document page) throws ElementNotFoundException {
        Elements generationButtons = page.selectXpath(GENERATION_QUERY);
        if (generationButtons.isEmpty()) {
            throw new ElementNotFoundException("document", GENERATION_QUERY, ENTRY_URL);
        }
        Set<String> seriesLinks = new LinkedHashSet<>();
        for (Element generationBtn : generationButtons) {
            // Extract page specific IDs from selector Parents
            String generationLabel = generationBtn.attr("data-panel-key");
            if (generationLabel.isBlank()) {
                continue;
            }
            for (Element aSeries : page.selectXpath(String.format(SERIES_QUERY, generationLabel))) {
                String seriesLink = aSeries.attr("href");
                if (!seriesLink.isBlank()) {
                    seriesLinks.add(seriesLink);
                }
            }
        }
        if (seriesLinks.isEmpty()) {
            throw new ElementNotFoundException("document", String.format(SERIES_QUERY, "<generation>"), ENTRY_URL);
        }
        return new ArrayList<>(seriesLinks);
    }
}
