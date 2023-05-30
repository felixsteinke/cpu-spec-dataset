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
    int[] sourceUrl;

    private CsvColumnIndexMapping() {
    }

    public static CsvColumnIndexMapping AMD() {
        CsvColumnIndexMapping mapping = new CsvColumnIndexMapping();
        mapping.name = new int[]{2};
        mapping.manufacturer = new int[]{};
        mapping.productCollection = new int[]{4};
        mapping.cores = new int[]{10};
        mapping.threads = new int[]{11};
        mapping.baseFrequency = new int[]{13};
        mapping.maxFrequency = new int[]{14};
        mapping.tdp = new int[]{28, 29};
        mapping.launchDate = new int[]{9};
        mapping.sourceUrl = new int[]{};
        return mapping;
    }

    public static CsvColumnIndexMapping Intel() {
        CsvColumnIndexMapping mapping = new CsvColumnIndexMapping();
        mapping.name = new int[]{1};
        mapping.manufacturer = new int[]{2};
        mapping.productCollection = new int[]{180};
        mapping.cores = new int[]{34};
        mapping.threads = new int[]{223};
        mapping.baseFrequency = new int[]{27};
        mapping.maxFrequency = new int[]{28, 20};
        mapping.tdp = new int[]{140, 30, 32};
        mapping.launchDate = new int[]{18, 69};
        mapping.sourceUrl = new int[]{246};
        return mapping;
    }
}
