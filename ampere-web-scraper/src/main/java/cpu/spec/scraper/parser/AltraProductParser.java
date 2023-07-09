package cpu.spec.scraper.parser;

import cpu.spec.scraper.CpuSpecificationModel;
import cpu.spec.scraper.exception.ElementNotFoundException;
import cpu.spec.scraper.factory.JsoupFactory;
import cpu.spec.scraper.validator.JsoupValidator;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class AltraProductParser {
    private static final String ENTRY_URL = "https://amperecomputing.com/briefs/ampere-altra-family-product-brief";

    /**
     * @return list of cpu specification models
     * @throws IOException              if page cannot be retrieved
     * @throws ElementNotFoundException if element cannot be retrieved
     */
    public static List<CpuSpecificationModel> extractSpecification() throws IOException, ElementNotFoundException {
        Document page = JsoupFactory.getConnection(ENTRY_URL).get();
        JsoupValidator validator = new JsoupValidator(ENTRY_URL);

        Element mainDiv = validator.selectFirst(page, "div#workload-page-dynamic-zone-10");
        Element tableBody = validator.selectFirst(mainDiv, "tbody");

        List<CpuSpecificationModel> specifications = new ArrayList<>();
        for (var tr : validator.select(tableBody, "tr")) {
            specifications.add(parseSpecification(validator.select(tr, "td", 4)));
        }
        return specifications;
    }

    private static CpuSpecificationModel parseSpecification(Elements columns) {
        CpuSpecificationModel specification = new CpuSpecificationModel();
        specification.cpuName = columns.get(0).text().trim();
        specification.cores = columns.get(1).text().trim();
        specification.clockSpeed = columns.get(2).text().trim();
        specification.tdp = columns.get(3).text().trim();
        specification.sourceUrl = ENTRY_URL;
        return specification;
    }
}
