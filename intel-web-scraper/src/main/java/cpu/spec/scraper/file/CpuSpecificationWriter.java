package cpu.spec.scraper.file;

import cpu.spec.scraper.CpuSpecificationModel;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

public abstract class CpuSpecificationWriter {
    /**
     * @param cpuSpecifications list with objects to write as csv
     * @param filePath          like "../dataset/manufacturer-cpus.csv"
     * @throws IOException if target filePath is invalid
     */
    public static void writeCsvFile(List<CpuSpecificationModel> cpuSpecifications, String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath, StandardCharsets.UTF_8)) {
            List<String> keySequence = getKeySequence(cpuSpecifications);

            writer.append(String.format("%s,\"%s\",%s,\"%s\"\n",
                    "CpuId",
                    "CpuName",
                    writeKeyValues(keySequence),
                    "SourceUrl"));

            for (CpuSpecificationModel spec : cpuSpecifications) {
                writer.append(String.format("%s,\"%s\",%s,\"%s\"\n",
                        spec.id,
                        spec.cpuName,
                        writeMapValues(keySequence, spec.dataValues),
                        spec.sourceUrl));
            }
        }
    }

    private static ArrayList<String> getKeySequence(List<CpuSpecificationModel> cpuSpecifications){
        Set<String> keySet = new HashSet<>();
        for (CpuSpecificationModel spec : cpuSpecifications) {
            keySet.addAll(spec.dataValues.keySet());
        }
        ArrayList<String> keySequence = new ArrayList<>(keySet);
        keySequence.sort(String.CASE_INSENSITIVE_ORDER);
        return keySequence;
    }

    private static String writeKeyValues(List<String> keySequence){
        StringBuilder stringBuilder = new StringBuilder();
        for (String key : keySequence) {
            if (key == null){
                stringBuilder.append(",");
            } else {
                stringBuilder.append(key.replaceAll(",", ".")).append(",");
            }
        }
        if (stringBuilder.isEmpty()){
            return "";
        }
        return stringBuilder.substring(0, stringBuilder.length() - 1);
    }

    private static String writeMapValues(List<String> keySequence, Map<String, String> values){
        StringBuilder stringBuilder = new StringBuilder();
        for (String key : keySequence) {
            String value = values.get(key);
            if (value == null){
                stringBuilder.append(",");
            } else {
                stringBuilder.append(value.replaceAll(",", ".")).append(",");
            }
        }
        if (stringBuilder.isEmpty()){
            return "";
        }
        return stringBuilder.substring(0, stringBuilder.length() - 1);
    }
}
