package cpu.spec.scraper.parser;

import cpu.spec.scraper.CpuSpecificationModel;
import cpu.spec.scraper.exception.ElementNotFoundException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

public abstract class CpuSpecificationParser {
    /**
     * @param url <a href="https://www.cpubenchmark.net/cpu.php?cpu=Intel+Xeon+Platinum+8173M+%40+2.00GHz&id=3182">Cpu Benchmark Specification Page</a>
     * @return cpu specification model
     * @throws IOException              if page cannot be retrieved
     * @throws ElementNotFoundException if element cannot be retrieved
     */
    public static CpuSpecificationModel extractSpecification(String url) throws IOException, ElementNotFoundException {
        Document page = Jsoup.connect(url).get();
        CpuSpecificationModel specification = new CpuSpecificationModel();

        Element nameElement = page.selectFirst("span.cpuname");
        if (nameElement != null) {
            specification.cpuName = nameElement.text();
        } else {
            throw new ElementNotFoundException("Specification Page", "span.cpuname");
        }

        Element socketElement = page.selectFirst("div.left-desc-cpu > p:contains(Socket)");
        if (socketElement != null) {
            specification.socket = socketElement.ownText().trim();
        }

        Element clockSpeedElement = page.selectFirst("div.left-desc-cpu > p:contains(Clockspeed)");
        if (clockSpeedElement != null) {
            specification.clockSpeed = clockSpeedElement.ownText().trim();
        }

        Element turboSpeedElement = page.selectFirst("div.left-desc-cpu > p:contains(Turbo Speed)");
        if (turboSpeedElement != null) {
            specification.turboSpeed = turboSpeedElement.ownText().trim();
        }

        Element coresThreadsElement = page.selectFirst("div.left-desc-cpu > p.mobile-column");
        if (coresThreadsElement != null) {
            String[] values = coresThreadsElement.ownText().trim().split(" ");
            specification.cores = values[0];
            specification.threads = values[1];
        }

        Element releaseDateElement = page.selectFirst("div.desc-foot > p.alt:contains(CPU First Seen on Charts)");
        if (releaseDateElement != null) {
            specification.releaseDate = releaseDateElement.ownText().trim();
        }
        specification.sourceUrl = url;
        return specification;
    }

}
