package cpu.spec.scraper.parser;

import cpu.spec.scraper.exception.ElementNotFoundException;
import cpu.spec.scraper.factory.JsoupFactory;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.util.*;

/**
 * Parses the AMD specification pages, which embed all processors as JSON in the "data-json" attribute of the
 * specification table (the same data the "Export" button of the page uses).
 */
public abstract class SpecificationTableParser {
    public static final String SOURCE_URL_COLUMN = "SourceUrl";
    private static final String TABLE_QUERY = "div.product-specs-table [data-json]";
    private static final JsonMapper JSON = JsonMapper.builder().build();

    /**
     * @param header column labels in order of first appearance
     * @param rows   processors as (label, display value) including {@link #SOURCE_URL_COLUMN}
     */
    public record Table(List<String> header, List<Map<String, String>> rows) {
    }

    /**
     * @param url <a href="https://www.amd.com/en/products/specifications/processors.html">AMD Specification Page</a>
     * @return all processors of the page
     * @throws IOException              if page cannot be retrieved
     * @throws ElementNotFoundException if the embedded specification data cannot be found
     */
    public static Table extractTable(String url) throws IOException, ElementNotFoundException {
        Document page = JsoupFactory.getConnection(url).maxBodySize(0).timeout(120_000).get();
        Element table = page.selectFirst(TABLE_QUERY);
        if (table == null) {
            throw new ElementNotFoundException("document", TABLE_QUERY, url);
        }
        return parseTable(table.attr("data-json"));
    }

    /**
     * @param json content of the "data-json" attribute ({"items": [{"elements": {...}, "productPages": {...}}]})
     * @return processors with display values like on the website (e.g. "Up to 5.7 GHz", "170W")
     */
    public static Table parseTable(String json) {
        JsonNode items = JSON.readTree(json).path("items");
        Set<String> header = new LinkedHashSet<>();
        List<Map<String, String>> rows = new ArrayList<>();
        for (JsonNode item : items) {
            JsonNode elements = item.path("elements");
            Map<String, String> row = new LinkedHashMap<>();
            for (String key : elementKeys(item)) {
                JsonNode element = elements.path(key);
                String label = Parser.unescapeEntities(element.path("title").asString(key), false).trim();
                header.add(label);
                row.put(label, displayValue(element));
            }
            row.put(SOURCE_URL_COLUMN, item.path("productPages").path("en").asString(""));
            rows.add(row);
        }
        header.add(SOURCE_URL_COLUMN);
        return new Table(new ArrayList<>(header), rows);
    }

    /**
     * @return "prefix + value + suffix" with multiple values joined by " , " (empty if there is no value)
     */
    static String displayValue(JsonNode element) {
        String value = joinValue(element.path("formatValue"));
        if (value.isEmpty()) {
            value = joinValue(element.path("value"));
        }
        if (value.isEmpty()) {
            return "";
        }
        String text = element.path("prefix").asString("") + value + element.path("suffix").asString("");
        return Parser.unescapeEntities(text, false).trim();
    }

    private static String joinValue(JsonNode value) {
        if (value.isArray()) {
            List<String> values = new ArrayList<>();
            value.forEach(v -> {
                if (!v.asString("").isBlank()) values.add(v.asString().trim());
            });
            return String.join(" , ", values);
        }
        return value.isValueNode() ? value.asString("").trim() : "";
    }

    private static List<String> elementKeys(JsonNode item) {
        List<String> keys = new ArrayList<>();
        item.path("elementsOrder").forEach(key -> keys.add(key.asString()));
        item.path("elements").propertyNames().forEach(key -> {
            if (!keys.contains(key)) keys.add(key);
        });
        return keys;
    }
}
