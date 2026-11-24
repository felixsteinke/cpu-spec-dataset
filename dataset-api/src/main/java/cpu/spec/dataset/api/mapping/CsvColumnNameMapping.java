package cpu.spec.dataset.api.mapping;

/**
 * Column mapping by CSV header name. Each field lists header names in fallback order;
 * the first column with a non-blank value in a row is used.
 * <p>
 * Header names are matched normalized (case-insensitive, ignoring symbols like ®, ™, ‡, quotes and whitespace),
 * so the mapping stays valid when the column order of a regenerated dataset changes.
 */
public class CsvColumnNameMapping {
    public String[] name;
    public String[] productCollection;
    public String[] cores;
    public String[] threads;
    public String[] baseFrequency;
    public String[] maxFrequency;
    public String[] tdp;
    public String[] launchDate;
    public String[] sourceUrl;
}
