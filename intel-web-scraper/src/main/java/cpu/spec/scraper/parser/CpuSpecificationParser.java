package cpu.spec.scraper.parser;

import cpu.spec.scraper.CpuSpecificationModel;
import cpu.spec.scraper.exception.ElementNotFoundException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

public abstract class CpuSpecificationParser {
    /**
     * @param url <a href="https://ark.intel.com/content/www/us/en/ark/products/226449/intel-pentium-gold-processor-8500-8m-cache-up-to-4-40-ghz.html">Intel Processor Specification Page</a>
     * @return cpu specification model
     * @throws IOException              if page cannot be retrieved
     * @throws ElementNotFoundException if element cannot be retrieved
     */
    public static CpuSpecificationModel extractSpecification(String url) throws IOException, ElementNotFoundException {
        Document page = Jsoup.connect(url).get();
        CpuSpecificationModel specification = new CpuSpecificationModel();

        Element titleElement = page.selectFirst("div.product-family-title-text > h1");
        if (titleElement == null) {
            throw new ElementNotFoundException("Specification Page", "div.product-family-title-text > h1");
        }
        specification.cpuName = titleElement.text();
        specification.manufacturerName = "intel";
        specification.productCollection = selectText(page, "span.value[data-key=ProductGroup] > a");
        specification.totalCores = selectText(page, "span.value[data-key=CoreCount]");
        specification.totalThreads = selectText(page, "span.value[data-key=ThreadCount]");
        specification.baseFrequency = selectText(page, "span.value[data-key=ClockSpeed]");
        specification.turboFrequency = selectText(page, "span.value[data-key=ClockSpeedMax]");
        specification.defaultTdp = selectText(page, "span.value[data-key=MaxTDP]");
        specification.launchDate = selectText(page, "span.value[data-key=BornOnDate]");
        specification.maxRam = selectText(page, "span.value[data-key=MaxMem]");
        specification.usageType = selectText(page, "span.value[data-key=CertifiedUseConditions]");

        return specification;
    }

    private static String selectText(Document document, String cssQuery) {
        Element element = document.selectFirst(cssQuery);
        return element != null ? element.text() : null;
    }
}
