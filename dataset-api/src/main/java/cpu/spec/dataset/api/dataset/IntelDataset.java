package cpu.spec.dataset.api.dataset;

import cpu.spec.dataset.api.mapping.CsvColumnModification;
import cpu.spec.dataset.api.mapping.CsvColumnNameMapping;

import java.time.Year;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IntelDataset extends Dataset {
    public IntelDataset() {
        super("dataset/intel-cpus.csv");
    }

    /**
     * @return column mapping by header names with fallbacks for cpu data (readonly)
     */
    @Override
    public CsvColumnNameMapping getColumnNameMapping() {
        CsvColumnNameMapping mapping = new CsvColumnNameMapping();
        mapping.name = new String[]{"CpuName"};
        mapping.productCollection = new String[]{"Product Collection"};
        mapping.cores = new String[]{"Total Cores"};
        mapping.threads = new String[]{"Total Threads"};
        mapping.baseFrequency = new String[]{
                "Processor Base Frequency",
                "Performance-core Base Frequency"};
        mapping.maxFrequency = new String[]{
                "Max Turbo Frequency",
                "Intel® Turbo Boost Technology 2.0 Frequency‡",
                "Performance-core Max Turbo Frequency",
                "Burst Frequency"};
        mapping.tdp = new String[]{
                "TDP",
                "Processor Base Power",
                "Configurable TDP-up",
                "Configurable TDP-down",
                "Scenario Design Power (SDP)"};
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
        modification.manufacturer = (s -> "intel");
        modification.productCollection = (s -> {
            if (s == null) return null;
            return s.replaceAll("\"", "");
        });
        Function<String, String> frequencyFunction = (s -> {
            if (s == null) return null;
            if (s.contains("GHz")) {
                s = s.replaceAll("GHz", "").trim();
                int mhz = Math.round(Float.parseFloat(s) * 1000);
                return Integer.toString(mhz);
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
            return s.replaceAll("W", "").trim();
        });
        modification.launchDate = (s -> {
            if (s == null) return null;
            // example: Q1'25 or Q4'99
            Matcher matcher = Pattern.compile("\\d{2}").matcher(s);
            if (matcher.find()) {
                int year = Integer.parseInt(matcher.group());
                int currentYear = Year.now().getValue() % 100;
                return Integer.toString(year <= currentYear ? 2000 + year : 1900 + year);
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
