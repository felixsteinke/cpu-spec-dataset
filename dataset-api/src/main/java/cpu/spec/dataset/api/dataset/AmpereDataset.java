package cpu.spec.dataset.api.dataset;

import cpu.spec.dataset.api.mapping.CsvColumnIndexMapping;
import cpu.spec.dataset.api.mapping.CsvColumnModification;

public class AmpereDataset extends Dataset {
    public AmpereDataset() {
        super("dataset/ampere-cpus.csv");
    }

    /**
     * @return column mapping with fallbacks for cpu data (readonly)
     */
    @Override
    public CsvColumnIndexMapping getColumnMapping() {
        CsvColumnIndexMapping mapping = new CsvColumnIndexMapping();
        mapping.name = new int[]{0};
        mapping.productCollection = new int[]{};
        mapping.cores = new int[]{2};
        mapping.threads = new int[]{};
        mapping.baseFrequency = new int[]{1};
        mapping.maxFrequency = new int[]{};
        mapping.tdp = new int[]{3};
        mapping.launchDate = new int[]{};
        mapping.sourceUrl = new int[]{4};
        return mapping;
    }

    /**
     * @return modifications for cpu data (readonly)
     */
    @Override
    public CsvColumnModification getColumnModifications() {
        CsvColumnModification modification = new CsvColumnModification();
        modification.name = null;
        modification.productCollection = null;
        modification.cores = null;
        modification.threads = null;
        modification.baseFrequency = (s -> {
            int mhz = (int) Float.parseFloat(s) * 1000;
            return Integer.toString(mhz);
        });
        modification.maxFrequency = null;
        modification.tdp = null;
        modification.launchDate = null;
        modification.sourceUrl = null;
        return modification;
    }
}
