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
    Function<String, String> maxRam = (s -> s.replaceAll(" GB", ""));
}
