package cpu.spec.dataset.api.dataset;

import cpu.spec.dataset.api.mapping.CsvColumnModification;
import cpu.spec.dataset.api.mapping.CsvColumnNameMapping;

public class AmpereDataset extends Dataset {
    public AmpereDataset() {
        super("dataset/ampere-cpus.csv");
    }

    /**
     * @return column mapping by header names with fallbacks for cpu data (readonly)
     */
    @Override
    public CsvColumnNameMapping getColumnNameMapping() {
        CsvColumnNameMapping mapping = new CsvColumnNameMapping();
        mapping.name = new String[]{"CpuName"};
        mapping.productCollection = new String[]{"Family"};
        mapping.cores = new String[]{"Cores"};
        mapping.threads = new String[]{};
        mapping.baseFrequency = new String[]{"ClockSpeed"};
        mapping.maxFrequency = new String[]{};
        mapping.tdp = new String[]{"TDP"};
        mapping.launchDate = new String[]{};
        mapping.sourceUrl = new String[]{"SourceUrl"};
        return mapping;
    }

    /**
     * @return modifications for cpu data (readonly)
     */
    @Override
    public CsvColumnModification getColumnModifications() {
        CsvColumnModification modification = new CsvColumnModification();
        // Altra briefs only list the model (e.g. "M128-30"), AmpereOne briefs the full name (e.g. "AmpereOne® A192-32X")
        modification.name = (s -> {
            if (s == null) return null;
            return s.startsWith("Ampere") ? s : "Ampere Altra " + s;
        });
        modification.manufacturer = (s -> "ampere");
        modification.productCollection = null;
        modification.cores = null;
        modification.threads = null;
        modification.baseFrequency = (s -> {
            if (s == null) return null;
            int mhz = Math.round(Float.parseFloat(s) * 1000);
            return Integer.toString(mhz);
        });
        modification.maxFrequency = null;
        modification.tdp = null;
        modification.launchDate = null;
        modification.sourceUrl = null;
        return modification;
    }
}
