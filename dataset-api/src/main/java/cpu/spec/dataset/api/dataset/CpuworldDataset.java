package cpu.spec.dataset.api.dataset;

import cpu.spec.dataset.api.mapping.CsvColumnModification;
import cpu.spec.dataset.api.mapping.CsvColumnNameMapping;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CpuworldDataset extends Dataset {

    public CpuworldDataset() {
        super("dataset/cpuworld-cpus.csv");
    }

    /**
     * @return column mapping by header names with fallbacks for cpu data (readonly)
     */
    @Override
    public CsvColumnNameMapping getColumnNameMapping() {
        CsvColumnNameMapping mapping = new CsvColumnNameMapping();
        mapping.name = new String[]{"CpuName"};
        mapping.productCollection = new String[]{"Microarchitecture"};
        mapping.cores = new String[]{"The number of CPU cores"};
        mapping.threads = new String[]{"The number of threads"};
        mapping.baseFrequency = new String[]{"Frequency"};
        mapping.maxFrequency = new String[]{"Maximum turbo frequency"};
        mapping.tdp = new String[]{"Thermal Design Power"};
        mapping.launchDate = new String[]{};
        mapping.sourceUrl = new String[]{"SourceUrl"};
        return mapping;
    }

    /**
     * @return modifications for cpu data (readonly)
     */
    @Override
    public CsvColumnModification getColumnModifications() {
        CsvColumnModification modification = new CsvColumnModification();
        modification.name = (s -> {
            if (s == null) return null;
            return s.replaceAll("\"", "");
        });
        Function<String, String> frequencyFunction = (s -> {
            if (s == null) return null;
            if (s.contains("MHz")) {
                Matcher matcher = Pattern.compile("\\d+ MHz").matcher(s);
                if (matcher.find()) {
                    return matcher.group().replaceAll("MHz", "").trim();
                }
            }
            if (s.contains("GHz")) {
                Matcher matcher = Pattern.compile("\\d+.\\d+ GHz").matcher(s);
                if (matcher.find()) {
                    String ghz = matcher.group().replaceAll("GHz", "").trim();
                    int mhz = Math.round(Float.parseFloat(ghz) * 1000);
                    return Integer.toString(mhz);
                }
            }
            return s;
        });
        modification.baseFrequency = frequencyFunction;
        modification.maxFrequency = frequencyFunction;
        modification.tdp = (s -> {
            if (s == null) return null;
            Matcher matcher = Pattern.compile("\\d+").matcher(s);
            if (matcher.find()) {
                return matcher.group();
            } else {
                return null;
            }
        });
        modification.sourceUrl = (s -> {
            if (s == null) return null;
            return s.replaceAll("\"", "");
        });
        return modification;
    }
}
