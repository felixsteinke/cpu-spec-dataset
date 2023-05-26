package cpu.spec.dataset.api.mapping;

public class CsvColumnIndexMapping {
    int[] name;
    int[] manufacturer;
    int[] productCollection;
    int[] cores;
    int[] threads;
    int[] baseFrequency;
    int[] maxFrequency;
    int[] tdp;
    int[] launchDate;
    int[] maxRam;

    private CsvColumnIndexMapping() {
    }

    public static CsvColumnIndexMapping AMD() {
        CsvColumnIndexMapping mapping = new CsvColumnIndexMapping();
        mapping.name = new int[]{2};
        mapping.manufacturer = new int[]{3};
        mapping.productCollection = new int[]{4};
        mapping.cores = new int[]{10};
        mapping.threads = new int[]{11};
        mapping.baseFrequency = new int[]{13};
        mapping.maxFrequency = new int[]{14};
        mapping.tdp = new int[]{28};
        mapping.launchDate = new int[]{9};
        mapping.maxRam = new int[]{};
        return mapping;
    }

    public static CsvColumnIndexMapping Intel() {
        CsvColumnIndexMapping mapping = new CsvColumnIndexMapping();
        mapping.name = new int[]{0};
        mapping.manufacturer = new int[]{1};
        mapping.productCollection = new int[]{2};
        mapping.cores = new int[]{3};
        mapping.threads = new int[]{4};
        mapping.baseFrequency = new int[]{5};
        mapping.maxFrequency = new int[]{6};
        mapping.tdp = new int[]{7};
        mapping.launchDate = new int[]{8};
        mapping.maxRam = new int[]{9};
        return mapping;
    }
}
