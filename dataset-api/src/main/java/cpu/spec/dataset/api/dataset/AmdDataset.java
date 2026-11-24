package cpu.spec.dataset.api.dataset;

import cpu.spec.dataset.api.mapping.CsvColumnModification;
import cpu.spec.dataset.api.mapping.CsvColumnNameMapping;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AmdDataset extends Dataset {
    public AmdDataset() {
        super("dataset/amd-cpus.csv");
    }

    /**
     * @return column mapping by header names with fallbacks for cpu data (readonly)
     */
    @Override
    public CsvColumnNameMapping getColumnNameMapping() {
        CsvColumnNameMapping mapping = new CsvColumnNameMapping();
        mapping.name = new String[]{"Name"};
        mapping.productCollection = new String[]{"Series", "Family"};
        mapping.cores = new String[]{"# of CPU Cores"};
        mapping.threads = new String[]{"# of Threads"};
        mapping.baseFrequency = new String[]{"Base Clock"};
        mapping.maxFrequency = new String[]{"Max. Boost Clock"};
        mapping.tdp = new String[]{"Default TDP", "AMD Configurable TDP (cTDP)"};
        mapping.launchDate = new String[]{"Launch Date"};
        mapping.sourceUrl = new String[]{};
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
        modification.manufacturer = (s -> "amd");
        modification.productCollection = (s -> {
            if (s == null) return null;
            return s.replaceAll("\"", "");
        });
        Function<String, String> frequencyFunction = (s -> {
            if (s == null) return null;
            if (s.contains("GHz")) {
                Matcher matcher = Pattern.compile("\\d+(\\.\\d+)?").matcher(s);
                if (matcher.find()) {
                    int mhz = Math.round(Float.parseFloat(matcher.group()) * 1000);
                    return Integer.toString(mhz);
                } else {
                    return null;
                }

            } else if (s.contains("MHz")) {
                return s.replaceAll("MHz", "").trim();
            } else {
                return s;
            }
        });
        modification.baseFrequency = frequencyFunction;
        modification.maxFrequency = frequencyFunction;
        modification.tdp = (s -> {
            if (s == null) return null;
            s = s.replaceAll("W", "").replaceAll("\\+", "");
            if (s.contains("-")) {
                s = s.split("-")[1];
            }
            if (s.contains("/")) {
                s = s.split("/")[1];
            }
            return s;
        });
        modification.launchDate = (s -> {
            if (s == null) return null;
            Matcher matcher = Pattern.compile("\\d{4}").matcher(s);
            if (matcher.find()) {
                return matcher.group();
            } else {
                return null;
            }
        });
        modification.sourceUrl = (s -> "https://www.amd.com/en/products/specifications/processors.html");
        return modification;
    }
}
