package cpu.spec.dataset.api;

import cpu.spec.dataset.api.mapping.CsvColumnIndexMapping;
import cpu.spec.dataset.api.mapping.CsvColumnModification;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@Getter
@NoArgsConstructor
@Entity(name = "CpuSpecification")
@Table(name = "cpu_specifications")
public class CpuSpecification {
    @Id
    @Column(name = "name")
    private String name;
    @Column(name = "name_qualifier")
    private String nameQualifier;
    @Column(name = "name_index")
    private Integer nameIndex;
    @Column(name = "manufacturer")
    private String manufacturer;
    @Column(name = "product_collection")
    private String productCollection;
    @Column(name = "product_collection_qualifier")
    private String productCollectionQualifier;
    @Column(name = "product_collection_index")
    private Integer productCollectionIndex;
    @Column(name = "cores")
    private Integer cores;
    @Column(name = "cores_qualifier")
    private String coresQualifier;
    @Column(name = "cores_index")
    private Integer coresIndex;
    @Column(name = "threads")
    private Integer threads;
    @Column(name = "threads_qualifier")
    private String threadsQualifier;
    @Column(name = "threads_index")
    private Integer threadsIndex;
    @Column(name = "base_frequency")
    private Integer baseFrequency;
    @Column(name = "base_frequency_qualifier")
    private String baseFrequencyQualifier;
    @Column(name = "base_frequency_index")
    private Integer baseFrequencyIndex;
    @Column(name = "max_frequency")
    private Integer maxFrequency;
    @Column(name = "max_frequency_qualifier")
    private String maxFrequencyQualifier;
    @Column(name = "max_frequency_index")
    private Integer maxFrequencyIndex;
    @Column(name = "tdp")
    private Integer tdp;
    @Column(name = "tdp_qualifier")
    private String tdpQualifier;
    @Column(name = "tdp_index")
    private Integer tdpIndex;
    @Column(name = "launch_year")
    private Integer launchYear;
    @Column(name = "launch_year_qualifier")
    private String launchYearQualifier;
    @Column(name = "launch_year_index")
    private Integer launchYearIndex;
    @Column(name = "source_url")
    private String sourceUrl;
    @Column(name = "source_url_index")
    private Integer sourceUrlIndex;


    public CpuSpecification(String[] row, CsvColumnIndexMapping mapping) {
        nameIndex = getColumnIndex(row, mapping.name);
        productCollectionIndex = getColumnIndex(row, mapping.productCollection);
        coresIndex = getColumnIndex(row, mapping.cores);
        threadsIndex = getColumnIndex(row, mapping.threads);
        baseFrequencyIndex = getColumnIndex(row, mapping.baseFrequency);
        maxFrequencyIndex = getColumnIndex(row, mapping.maxFrequency);
        tdpIndex = getColumnIndex(row, mapping.tdp);
        launchYearIndex = getColumnIndex(row, mapping.launchDate);
        sourceUrlIndex = getColumnIndex(row, mapping.sourceUrl);
    }

    private static Integer getColumnIndex(String[] row, int[] indexOptions) {
        if (indexOptions == null) {
            return null;
        }
        for (int index : indexOptions) {
            if (!row[index].isBlank()) {
                return index;
            }
        }
        return null;
    }

    public static String getColumnValue(String[] row, Integer index) {
        if (index == null || index >= row.length) {
            return null;
        }
        return row[index];
    }

    private static String transformValue(String columnValue, Function<String, String> modification) {
        if (columnValue == null) {
            return null;
        }
        if (modification == null) {
            return columnValue;
        } else {
            return modification.apply(columnValue);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        CpuSpecification that = (CpuSpecification) o;
        return name != null && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    private static Integer transformIntValue(String columnValue, Function<String, String> modification) {
        if (columnValue == null) {
            return null;
        } else {
            try {
                return Integer.parseInt(transformValue(columnValue, modification));
            } catch (NumberFormatException e) {
                return null;
            }
        }
    }

    private static Integer transformFloatValue(String columnValue, Function<String, String> modification) {
        if (columnValue == null) {
            return null;
        } else {
            try {
                return (int) Math.ceil(Double.parseDouble(transformValue(columnValue, modification)));
            } catch (NumberFormatException e) {
                return null;
            }
        }
    }

    public void updateValues(String[] row, CsvColumnModification modification) {
        name = transformValue(getColumnValue(row, nameIndex), modification.name);
        productCollection = transformValue(getColumnValue(row, productCollectionIndex), modification.productCollection);
        cores = transformIntValue(getColumnValue(row, coresIndex), modification.cores);
        threads = transformIntValue(getColumnValue(row, threadsIndex), modification.threads);
        baseFrequency = transformIntValue(getColumnValue(row, baseFrequencyIndex), modification.baseFrequency);
        maxFrequency = transformIntValue(getColumnValue(row, maxFrequencyIndex), modification.maxFrequency);
        tdp = transformFloatValue(getColumnValue(row, tdpIndex), modification.tdp);
        launchYear = transformIntValue(getColumnValue(row, launchYearIndex), modification.launchDate);
        sourceUrl = transformValue(getColumnValue(row, sourceUrlIndex), modification.sourceUrl);
    }

    public void updateQualifier(Map<Integer, String> columns) {
        nameQualifier = columns.getOrDefault(nameIndex, null);
        productCollectionQualifier = columns.getOrDefault(productCollectionIndex, null);
        coresQualifier = columns.getOrDefault(coresIndex, null);
        threadsQualifier = columns.getOrDefault(threadsIndex, null);
        baseFrequencyQualifier = columns.getOrDefault(baseFrequencyIndex, null);
        maxFrequencyQualifier = columns.getOrDefault(maxFrequencyIndex, null);
        tdpQualifier = columns.getOrDefault(tdpIndex, null);
        launchYearQualifier = columns.getOrDefault(launchYearIndex, null);
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }
}
