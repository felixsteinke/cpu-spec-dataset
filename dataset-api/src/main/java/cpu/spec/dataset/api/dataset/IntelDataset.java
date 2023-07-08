package cpu.spec.dataset.api.dataset;

import cpu.spec.dataset.api.mapping.CsvColumnIndexMapping;
import cpu.spec.dataset.api.mapping.CsvColumnModification;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IntelDataset extends Dataset {
    public IntelDataset() {
        super("dataset/intel-cpus.csv");
    }

    /**
     * @return column mapping with fallbacks for cpu data (readonly)
     */
    @Override
    public CsvColumnIndexMapping getColumnMapping() {
        CsvColumnIndexMapping mapping = new CsvColumnIndexMapping();
        mapping.name = new int[]{1};
        mapping.productCollection = new int[]{179};
        mapping.cores = new int[]{33};
        mapping.threads = new int[]{222};
        mapping.baseFrequency = new int[]{26};
        mapping.maxFrequency = new int[]{27, 19};
        mapping.tdp = new int[]{139, 29, 31, 194};
        mapping.launchDate = new int[]{17, 68};
        mapping.sourceUrl = new int[]{245};
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
        modification.tdp = (s -> s.replaceAll("W", "").trim());
        modification.launchDate = (s -> {
            Matcher matcher = Pattern.compile("\\d{2}").matcher(s);
            if (matcher.find()) {
                return "20" + matcher.group();
            } else {
                return null;
            }
        });
        modification.sourceUrl = (s -> s.substring(1, s.length() - 1));
        return modification;
    }
}
