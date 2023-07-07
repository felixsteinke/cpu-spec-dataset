package cpu.spec.dataset.api.registry;

import cpu.spec.dataset.api.mapping.CsvColumnIndexMapping;
import cpu.spec.dataset.api.mapping.CsvColumnModification;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CpuworldDataset extends Dataset {

    public CpuworldDataset() {
        super("dataset/cpuworld-cpus.csv");
    }

    /**
     * @return column mapping with fallbacks for cpu data (readonly)
     */
    @Override
    public CsvColumnIndexMapping getColumnMapping() {
        CsvColumnIndexMapping mapping = new CsvColumnIndexMapping();
        mapping.name = new int[]{1};
        mapping.productCollection = new int[]{18};
        mapping.cores = new int[]{25};
        mapping.threads = new int[]{26};
        mapping.baseFrequency = new int[]{9};
        mapping.maxFrequency = new int[]{17};
        mapping.tdp = new int[]{27};
        mapping.launchDate = new int[]{};
        mapping.sourceUrl = new int[]{29};
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
                    int mhz = (int) Float.parseFloat(s) * 1000;
                    return Integer.toString(mhz);
                }
            }
            return s;
        });
        modification.baseFrequency = frequencyFunction;
        modification.maxFrequency = frequencyFunction;
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
