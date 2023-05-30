package cpu.spec.dataset.api.mapping;

import java.util.function.Function;

public class CsvColumnModification {
    Function<String, String> name;
    Function<String, String> manufacturer;
    Function<String, String> productCollection;
    Function<String, String> cores;
    Function<String, String> threads;
    Function<String, String> baseFrequency;
    Function<String, String> maxFrequency;
    Function<String, String> tdp;
    Function<String, String> launchDate;

    private CsvColumnModification() {
    }

    public static CsvColumnModification AMD() {
        CsvColumnModification modification = new CsvColumnModification();
        return modification;
    }

    public static CsvColumnModification Intel() {
        CsvColumnModification modification = new CsvColumnModification();
        modification.name = (s -> s.replaceAll("\"", ""));
        modification.manufacturer = (s -> s.replaceAll("\"", ""));
        modification.productCollection = (s -> s.replaceAll("\"", ""));
        modification.cores = (s -> s.replaceAll("\"", ""));
        modification.threads = (s -> s.replaceAll("\"", ""));
        modification.baseFrequency = (s -> s.replaceAll("\"", ""));
        modification.maxFrequency = (s -> s.replaceAll("\"", ""));
        modification.tdp = (s -> s.replaceAll("\"", ""));
        modification.launchDate = (s -> s.replaceAll("\"", ""));
        return modification;
    }
}
