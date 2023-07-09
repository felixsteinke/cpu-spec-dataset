package cpu.spec.dataset.api.mapping;

import java.util.function.Function;

public class CsvColumnModification {
    public Function<String, String> name;
    public Function<String, String> manufacturer;
    public Function<String, String> productCollection;
    public Function<String, String> cores;
    public Function<String, String> threads;
    public Function<String, String> baseFrequency;
    public Function<String, String> maxFrequency;
    public Function<String, String> tdp;
    public Function<String, String> launchDate;
    public Function<String, String> sourceUrl;
}
