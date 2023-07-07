package cpu.spec.dataset.api.registry;

import cpu.spec.dataset.api.mapping.CsvColumnIndexMapping;
import cpu.spec.dataset.api.mapping.CsvColumnModification;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BenchmarkDataset extends Dataset {

    public BenchmarkDataset() {
        super("dataset/benchmark-cpus.csv");
    }

    /**
     * @return column mapping with fallbacks for cpu data (readonly)
     */
    @Override
    public CsvColumnIndexMapping getColumnMapping() {
        CsvColumnIndexMapping mapping = new CsvColumnIndexMapping();
        mapping.name = new int[]{0};
        mapping.productCollection = new int[]{};
        mapping.cores = new int[]{4};
        mapping.threads = new int[]{5};
        mapping.baseFrequency = new int[]{2};
        mapping.maxFrequency = new int[]{3};
        mapping.tdp = new int[]{6};
        mapping.launchDate = new int[]{7};
        mapping.sourceUrl = new int[]{8};
        return mapping;
    }

    /**
     * @return modifications for cpu data (readonly)
     */
    @Override
    public CsvColumnModification getColumnModifications() {
        CsvColumnModification modification = new CsvColumnModification();
        modification.name = (s -> s.replaceAll("\"", ""));

        Function<String, String> frequencyFunction = (s -> {
            if (s.contains("GHz")) {
                s = s.replaceAll("GHz", "").trim();
                if (s.endsWith(".0")) {
                    s = s.substring(0, s.length() - 2);
                }
                int mhz = (int) Float.parseFloat(s) * 1000;
                return Integer.toString(mhz);
            } else if (s.contains("MHz")) {
                return s.replaceAll("MHz", "").trim();
            } else {
                return s;
            }
        });
        modification.baseFrequency = frequencyFunction;
        modification.maxFrequency = frequencyFunction;
        modification.launchDate = (s -> {
            Matcher matcher = Pattern.compile("\\d{4}").matcher(s);
            if (matcher.find()) {
                return matcher.group();
            } else {
                return null;
            }
        });
        modification.tdp = (s -> {
            Matcher matcher = Pattern.compile("\\d+").matcher(s);
            if (matcher.find()) {
                return matcher.group();
            } else {
                return null;
            }
        });
        modification.sourceUrl = (s -> s.substring(1, s.length() - 1));
        return modification;
    }
}
