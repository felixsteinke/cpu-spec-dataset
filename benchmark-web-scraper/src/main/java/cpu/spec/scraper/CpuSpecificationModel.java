package cpu.spec.scraper;

import java.util.List;

/**
 * One row of the CPU mega page (values as written to the csv, empty if unknown).
 * The first columns keep the order and value format of the previous dataset (e.g. "3.4 GHz", "65 W", "Q2 2018").
 */
public record CpuSpecificationModel(String cpuName, String socket, String clockSpeed, String turboSpeed, String cores,
                                    String threads, String tdp, String releaseDate, String sourceUrl, String id,
                                    String category, String cpuCount, String cpuMark, String threadMark, String price) {
    public static final List<String> HEADER = List.of("CpuName", "Socket", "ClockSpeed", "TurboSpeed", "Cores",
            "Threads", "TDP", "ReleaseDate", "SourceUrl", "CpuId", "Category", "CpuCount", "CpuMark", "ThreadMark",
            "Price");

    public List<String> toRow() {
        return List.of(cpuName, socket, clockSpeed, turboSpeed, cores, threads, tdp, releaseDate, sourceUrl, id,
                category, cpuCount, cpuMark, threadMark, price);
    }
}
