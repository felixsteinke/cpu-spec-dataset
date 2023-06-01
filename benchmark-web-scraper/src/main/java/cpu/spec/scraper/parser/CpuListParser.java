package cpu.spec.scraper.parser;

import cpu.spec.scraper.exception.ElementNotFoundException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public abstract class CpuListParser {
    /**
     * @param url <a href="https://www.cpubenchmark.net/cpu_list.php">Cpu Benchmark Cpu Page</a>
     * @return cpu links for sub routing (replaced 'cpu_lookup.php' to 'cpu.php' and '%40' to '@')
     * @throws IOException              if page cannot be retrieved
     * @throws ElementNotFoundException if element cannot be retrieved
     */
    public static List<String> extractSpecificationLinks(String url) throws IOException, ElementNotFoundException {
        Document page = Jsoup.connect(url).get();

        Element tableBody = page.selectFirst("#cputable > tbody");
        if (tableBody == null) {
            throw new ElementNotFoundException("Cpu Table Page", "#cputable > tbody");
        }

        Elements tableRows = tableBody.select("tr");
        if (tableRows.isEmpty()) {
            throw new ElementNotFoundException("tbody", "tr");
        }

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
            specificationLinks.add(URLDecoder.decode(aSpec.attr("href").replaceAll("cpu_lookup.php", "cpu.php"), StandardCharsets.UTF_8));
        }
        return specificationLinks;
    }
}
