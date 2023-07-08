package cpu.spec.scraper.parser;

import cpu.spec.scraper.exception.ElementNotFoundException;
import cpu.spec.scraper.file.ResourceFileReader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static cpu.spec.scraper.validator.JsoupValidator.validate;

public abstract class CpuListParser {
    /**
     * @param resourceFilePath path within the resource directory
     * @return cpu links for sub routing (url decoded)
     * @throws IOException              if page cannot be retrieved
     * @throws ElementNotFoundException if element cannot be retrieved
     */
    public static List<String> extractSpecificationLinks(String resourceFilePath) throws IOException, ElementNotFoundException {
        Document page = Jsoup.parse(ResourceFileReader.getFile(resourceFilePath));

        Element tableBody = page.selectFirst("#cputable > tbody");
        validate(tableBody, "Page", "#cputable > tbody");

        Elements tableRows = tableBody.select("tr");
        validate(tableRows, "#cputable > tbody", "tr");

        List<String> specificationLinks = new ArrayList<>();
        for (Element row : tableRows) {
            Element aSpec = row.selectFirst("a");
            if (aSpec == null) {
                continue;
            }
            specificationLinks.add(URLDecoder.decode(aSpec.attr("href").replaceAll("cpu_lookup.php", "cpu.php"), StandardCharsets.UTF_8));
        }
        return specificationLinks;
    }
}
