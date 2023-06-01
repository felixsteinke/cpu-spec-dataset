package cpu.spec.scraper.file;

import cpu.spec.scraper.CpuSpecificationModel;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public abstract class CpuSpecificationWriter {
    /**
     * @param cpuSpecifications list with objects to write as csv
     * @param filePath          like "../dataset/manufacturer-cpus.csv"
     * @throws IOException if target filePath is invalid
     */
    public static void writeCsvFile(List<CpuSpecificationModel> cpuSpecifications, String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.append(String.format("%s,%s,%s,%s,%s,%s,%s,%s,\"%s\"\n",
                    "CpuName",
                    "Socket",
                    "ClockSpeed",
                    "TurboSpeed",
                    "Cores",
                    "Threads",
                    "TDP",
                    "ReleaseDate",
                    "SourceUrl"));

            for (CpuSpecificationModel spec : cpuSpecifications) {
                writer.append(String.format("%s,%s,%s,%s,%s,%s,%s,%s,\"%s\"\n",
                        cleanValue(spec.cpuName),
                        cleanValue(spec.socket),
                        cleanValue(spec.clockSpeed),
                        cleanValue(spec.turboSpeed),
                        cleanValue(spec.cores),
                        cleanValue(spec.threads),
                        cleanValue(spec.tdp),
                        cleanValue(spec.releaseDate),
                        spec.sourceUrl));
            }
        }
    }

    private static String cleanValue(String value){
        if (value == null) {
            return "";
        } else {
            return value.replaceAll(",", ".");
        }
    }
}
