package cpu.spec.scraper.parser;

import cpu.spec.scraper.CpuSpecificationModel;
import cpu.spec.scraper.exception.ElementNotFoundException;
import cpu.spec.scraper.factory.JsoupFactory;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;

/**
 * Parses the SKU table of an Ampere product brief (e.g. "Processor Model | Core Count | Freq (GHz) | Usage Power (W)").
 * The table is identified by its column names, so its position on the page does not matter.
 */
public abstract class ProductBriefParser {
    private static final String TABLE_DESCRIPTION = "table with columns 'core', 'freq' and 'power'";

    /**
     * @param family product family (e.g. "Ampere Altra")
     * @param url    <a href="https://amperecomputing.com/briefs/ampereone-family-product-brief">Ampere Product Brief</a>
     * @return cpu specifications of the SKU table
     * @throws IOException              if page cannot be retrieved
     * @throws ElementNotFoundException if no SKU table can be found
     */
    public static List<CpuSpecificationModel> extractSpecifications(String family, String url) throws IOException, ElementNotFoundException {
        Document page = JsoupFactory.getConnection(url).get();
        return parseSpecifications(page, family, url);
    }

    public static List<CpuSpecificationModel> parseSpecifications(Document page, String family, String url) throws ElementNotFoundException {
        for (Element table : page.select("table")) {
            Elements rows = table.select("tr");
            if (rows.size() < 2) continue;
            List<String> header = cellTexts(rows.getFirst());
            int cores = indexOf(header, h -> h.contains("core"));
            int frequency = indexOf(header, h -> h.contains("freq"));
            int power = indexOf(header, h -> h.contains("power"));
            if (cores < 0 || frequency < 0 || power < 0) continue;

            List<CpuSpecificationModel> specifications = new ArrayList<>();
            for (Element row : rows.subList(1, rows.size())) {
                List<String> cells = cellTexts(row);
                if (cells.size() <= Math.max(cores, Math.max(frequency, power)) || cells.getFirst().isBlank()) continue;
                specifications.add(new CpuSpecificationModel(cells.getFirst(), family,
                        cells.get(cores), cells.get(frequency), cells.get(power), url));
            }
            if (!specifications.isEmpty()) {
                return specifications;
            }
        }
        throw new ElementNotFoundException("document", TABLE_DESCRIPTION, url);
    }

    private static List<String> cellTexts(Element row) {
        return row.select("th, td").stream().map(cell -> cell.text().trim()).toList();
    }

    private static int indexOf(List<String> header, Predicate<String> matcher) {
        for (int i = 0; i < header.size(); i++) {
            if (matcher.test(header.get(i).toLowerCase(Locale.ROOT))) return i;
        }
        return -1;
    }
}
