package cpu.spec.dataset.api.dataset;

import cpu.spec.dataset.api.mapping.CsvColumnModification;
import cpu.spec.dataset.api.mapping.CsvColumnNameMapping;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BenchmarkDataset extends Dataset {

    public BenchmarkDataset() {
        super("dataset/benchmark-cpus.csv");
    }

    /**
     * @return column mapping by header names with fallbacks for cpu data (readonly)
     */
    @Override
    public CsvColumnNameMapping getColumnNameMapping() {
        CsvColumnNameMapping mapping = new CsvColumnNameMapping();
        mapping.name = new String[]{"CpuName"};
        mapping.productCollection = new String[]{};
        mapping.cores = new String[]{"Cores"};
        mapping.threads = new String[]{"Threads"};
        mapping.baseFrequency = new String[]{"ClockSpeed"};
        mapping.maxFrequency = new String[]{"TurboSpeed"};
        mapping.tdp = new String[]{"TDP"};
        mapping.launchDate = new String[]{"ReleaseDate"};
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
            if (s == null) return null;
            Matcher matcher = Pattern.compile("\\d{4}").matcher(s);
            if (matcher.find()) {
                return matcher.group();
            } else {
                return null;
            }
        });
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
            return s.substring(1, s.length() - 1);
        });
        return modification;
    }
}
