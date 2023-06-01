package cpu.spec.dataset.api.mapping;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CsvColumnModification {
    Function<String, String> name;
    Function<String, String> productCollection;
    Function<String, String> cores;
    Function<String, String> threads;
    Function<String, String> baseFrequency;
    Function<String, String> maxFrequency;
    Function<String, String> tdp;
    Function<String, String> launchDate;
    Function<String, String> sourceUrl;

    private CsvColumnModification() {
    }

    /**
     * @return modifications for amd cpu data (readonly)
     */
    public static CsvColumnModification AMD() {
        CsvColumnModification modification = new CsvColumnModification();
        modification.name = (s -> s.replaceAll("\"", ""));
        modification.productCollection = (s -> s.replaceAll("\"", ""));

        Function<String, String> frequencyFunction = (s -> {
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
            Matcher matcher = Pattern.compile("\\d{4}").matcher(s);
            if (matcher.find()) {
                return matcher.group();
            } else {
                return null;
            }
        });
        return modification;
    }

    /**
     * @return modifications for intel cpu data (readonly)
     */
    public static CsvColumnModification Intel() {
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

    public static CsvColumnModification CpuBenchmark() {
        CsvColumnModification modification = new CsvColumnModification();
        modification.name = (s -> s.replaceAll("\"", ""));

        Function<String, String> frequencyFunction = (s -> {
            if (s.contains("GHz")) {
                s = s.replaceAll("GHz", "").trim();
                if (s.endsWith(".0")){
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
