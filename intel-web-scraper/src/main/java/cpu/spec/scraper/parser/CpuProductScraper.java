package cpu.spec.scraper.parser;

import cpu.spec.scraper.exception.ElementNotFoundException;
import cpu.spec.scraper.factory.JsoupFactory;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static cpu.spec.scraper.validator.JsoupValidator.validate;

public abstract class CpuProductScraper {
    /**
     * @param url <a href="https://ark.intel.com/content/www/us/en/ark.html#@PanelLabel122139">Intel Processor Product Page</a>
     * @return series links for sub routing
     * @throws IOException              if page cannot be retrieved
     * @throws ElementNotFoundException if element cannot be retrieved
     */
    public static List<String> extractSeriesLinks(String url) throws IOException, ElementNotFoundException {
        Document page = JsoupFactory.getConnection(url).get();

        Elements generationButtons = page.select("div[data-parent-panel-key='Processors'] > div > div[data-panel-key]");
        validate(generationButtons, "Page", "div[data-parent-panel-key='Processors'] > div > div[data-panel-key]");

        List<String> seriesLinks = new ArrayList<>();
        for (Element generationBtn : generationButtons) {
            String generationLabel = generationBtn.attr("data-panel-key");
            if (generationLabel.isBlank()) {
                continue;
            }
            Elements linkElements = page.select(String.format("div[data-parent-panel-key='%s'] > div > div > span > a[href]", generationLabel));
            for (Element aSeries : linkElements) {
                String seriesLink = aSeries.attr("href");
                if (seriesLink.isBlank()) {
                    continue;
                }
                seriesLinks.add(seriesLink);
            }
        }
        return seriesLinks;
    }
}
