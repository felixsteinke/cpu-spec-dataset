package cpu.spec.dataset.api.mapping;

public class CsvColumnIndexMapping {
    int[] name;
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

    /**
     * @return column mapping with fallbacks for amd cpu data (readonly)
     */
    public static CsvColumnIndexMapping AMD() {
        CsvColumnIndexMapping mapping = new CsvColumnIndexMapping();
        mapping.name = new int[]{2};
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

    /**
     * @return column mapping with fallbacks for intel cpu data (readonly)
     */
    public static CsvColumnIndexMapping Intel() {
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
}
