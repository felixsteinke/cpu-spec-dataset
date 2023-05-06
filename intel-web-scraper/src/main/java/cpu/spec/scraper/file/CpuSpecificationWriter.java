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
            writer.append("cpuName,manufacturerName,productCollection,totalCores,totalThreads,baseFrequency,turboFrequency,defaultTdp,launchDate,maxRam,usageType\n");

            for (CpuSpecificationModel spec : cpuSpecifications) {
                writer.append(String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s\n",
                        spec.cpuName,
                        spec.manufacturerName,
                        spec.productCollection,
                        spec.totalCores,
                        spec.totalThreads,
                        spec.baseFrequency,
                        spec.turboFrequency,
                        spec.defaultTdp,
                        spec.launchDate,
                        spec.maxRam,
                        spec.usageType));
            }
        }
    }
}
