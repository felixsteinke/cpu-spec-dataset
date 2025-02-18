package cpu.spec.scraper.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cpu.spec.scraper.exception.ElementNotFoundException;
import cpu.spec.scraper.factory.JsoupFactory;


public abstract class CpuProductParser {
    private static final String ENTRY_URL = "https://www.intel.com/content/www/us/en/ark.html";

    /**
     * @return series links for sub routing
     * @throws IOException              if page cannot be retrieved
     * @throws ElementNotFoundException if element cannot be retrieved
     */
    public static List<String> extractSeriesLinks() throws IOException, ElementNotFoundException {
        Document page = JsoupFactory.getConnection(ENTRY_URL).get();
        
        // xPath: divs with data-parent-panel-key='Processors' -> divs with data-panel-key
        String xPathQuery = ".//div[@data-parent-panel-key='Processors']//div[@data-panel-key]";
        Elements generationButtons = page.selectXpath(xPathQuery);

        List<String> seriesLinks = new ArrayList<>();
        for (Element generationBtn : generationButtons) {

            // Extract page specific IDs from selector Parents
            String generationLabel = generationBtn.attr("data-panel-key"); //"abs:href");
            if (generationLabel.isBlank()) {
                continue;
            }
            
            // Extract links to Series
            // xPath: "a" childred of divs with generation label
            xPathQuery = String.format(".//div[@data-parent-panel-key='%s']//a", generationLabel);
            Elements linkElements = page.selectXpath(xPathQuery);
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
