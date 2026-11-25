package cpu.spec.dataset.api.dataset;

import cpu.spec.dataset.api.mapping.CsvColumnModification;
import cpu.spec.dataset.api.mapping.CsvColumnNameMapping;

import java.time.Year;
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
            // upper value of ranges and lists (e.g. "65W", "45-120W", "15/25W", "115 , 120W")
            Matcher matcher = Pattern.compile("\\d+(\\.\\d+)?").matcher(s);
            String last = null;
            while (matcher.find()) {
                last = matcher.group();
            }
            return last;
        });
        modification.launchDate = (s -> {
            if (s == null) return null;
            // e.g. "10/19/2023", "Q3 2016", "Q12021" (4-digit year) or "9/16/25", "Q217", "2Q18" (2-digit year)
            Matcher fullYear = Pattern.compile("(19|20)\\d{2}").matcher(s);
            if (fullYear.find()) {
                return fullYear.group();
            }
            Matcher shortYear = Pattern.compile("(\\d{2})\\s*$").matcher(s.trim());
            if (shortYear.find()) {
                int year = Integer.parseInt(shortYear.group(1));
                return Integer.toString(year <= Year.now().getValue() % 100 ? 2000 + year : 1900 + year);
            }
            return null;
        });
        modification.sourceUrl = (s -> {
            // product page, or the specification page if a processor has none
            if (s == null || s.isBlank()) return "https://www.amd.com/en/products/specifications/processors.html";
            return s;
        });
        return modification;
    }
}
