package cpu.spec.dataset.api.dataset;

import cpu.spec.dataset.api.mapping.CsvColumnIndexMapping;
import cpu.spec.dataset.api.mapping.CsvColumnModification;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AmdDataset extends Dataset {
    public AmdDataset() {
        super("dataset/amd-cpus.csv");
    }

    /**
     * @return column mapping with fallbacks for cpu data (readonly)
     */
    @Override
    public CsvColumnIndexMapping getColumnMapping() {
        CsvColumnIndexMapping mapping = new CsvColumnIndexMapping();
        mapping.name = new int[]{2};
        mapping.productCollection = new int[]{4};
        mapping.cores = new int[]{10};
        mapping.threads = new int[]{11};
        mapping.baseFrequency = new int[]{13};
        mapping.maxFrequency = new int[]{14};
        mapping.tdp = new int[]{28, 29};
        mapping.launchDate = new int[]{9};
        mapping.sourceUrl = new int[]{};
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
                    int mhz = (int) Float.parseFloat(matcher.group()) * 1000;
                    return Integer.toString(mhz);
                } else {
                    return null;
                }

            } else if (s.contains("MHz")) {
                return s.replaceAll("MHz", "");
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
        modification.sourceUrl = (s -> "https://www.amd.com/en/products/specifications/processors");
        return modification;
    }
}
