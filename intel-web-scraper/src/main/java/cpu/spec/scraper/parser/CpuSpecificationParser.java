package cpu.spec.scraper.parser;

import cpu.spec.scraper.CpuSpecificationModel;
import cpu.spec.scraper.exception.ElementNotFoundException;
import cpu.spec.scraper.factory.JsoupFactory;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public abstract class CpuSpecificationParser {
    // css: page title, its own text is the cpu name (without the nested description "Specifications")
    private static final String TITLE_QUERY = "h1.cmp-teaser__title";
    // css: rows of the specification sections (one label / value pair)
    private static final String ROW_QUERY = "div[id^=list-section-spec] tr";
    private static final String LABEL_QUERY = "th.key";
    private static final String DATA_QUERY = "td.value";

    /**
     * @param url <a href="https://www.intel.com/content/www/us/en/products/sku/201900/intel-pentium-gold-g6405t-processor-4m-cache-3-50-ghz/specifications.html">Intel Processor Specification Page</a>
     * @return cpu specification model
     * @throws IOException              if page cannot be retrieved
     * @throws ElementNotFoundException if element cannot be retrieved
     */
    public static CpuSpecificationModel extractSpecification(String url) throws IOException, ElementNotFoundException {
        Document page = JsoupFactory.getCrawlerConnection(url).get();
        return parseSpecification(page, url);
    }

    /**
     * @throws ElementNotFoundException if the title or the specification sections cannot be found
     */
    public static CpuSpecificationModel parseSpecification(Document page, String url) throws ElementNotFoundException {
        Element titleElement = page.selectFirst(TITLE_QUERY);
        if (titleElement == null || titleElement.ownText().isBlank()) {
            throw new ElementNotFoundException("document", TITLE_QUERY, url);
        }
        Elements specElements = page.select(ROW_QUERY);
        if (specElements.isEmpty()) {
            throw new ElementNotFoundException("document", ROW_QUERY, url);
        }

        CpuSpecificationModel specification = new CpuSpecificationModel();
        specification.id = selectId(url);
        specification.cpuName = titleElement.ownText().trim();
        specification.sourceUrl = url;
        for (Element specElement : specElements) {
            Element dataKeyElement = specElement.selectFirst(LABEL_QUERY);
            Element dataValue = specElement.selectFirst(DATA_QUERY);
            if (dataKeyElement != null && dataValue != null && !isKeyIgnored(dataKeyElement.text())) {
                specification.dataValues.put(dataKeyElement.text().trim(), dataValue.text().trim());
            }
        }
        return specification;
    }

    private static String selectId(String url) {
        try {
            return new URI(url).getPath().trim().split("/")[7];
        } catch (URISyntaxException | ArrayIndexOutOfBoundsException e) {
            return e.getClass().getSimpleName();
        }
    }

    private static boolean isKeyIgnored(String key) {
        return key.equalsIgnoreCase("DatasheetUrl");
    }
}
