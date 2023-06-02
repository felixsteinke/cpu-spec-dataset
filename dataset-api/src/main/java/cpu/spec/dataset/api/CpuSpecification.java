package cpu.spec.dataset.api;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;

import java.util.Objects;

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
    @Column(name = "manufacturer")
    private String manufacturer;
    @Column(name = "product_collection")
    private String productCollection;
    @Column(name = "product_collection_qualifier")
    private String productCollectionQualifier;
    @Column(name = "cores")
    private Integer cores;
    @Column(name = "cores_qualifier")
    private String coresQualifier;
    @Column(name = "threads")
    private Integer threads;
    @Column(name = "threads_qualifier")
    private String threadsQualifier;
    @Column(name = "base_frequency")
    private Integer baseFrequency;
    @Column(name = "base_frequency_qualifier")
    private String baseFrequencyQualifier;
    @Column(name = "max_frequency")
    private Integer maxFrequency;
    @Column(name = "max_frequency_qualifier")
    private String maxFrequencyQualifier;
    @Column(name = "tdp")
    private Integer tdp;
    @Column(name = "tdp_qualifier")
    private String tdpQualifier;
    @Column(name = "launch_year")
    private Integer launchYear;
    @Column(name = "launch_year_qualifier")
    private String launchYearQualifier;
    @Column(name = "source_url")
    private String sourceUrl;

    public void setName(String name) {
        this.name = name;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public void setProductCollection(String productCollection) {
        this.productCollection = productCollection;
    }

    public void setCores(String cores) {
        if (cores == null){
            this.cores = null;
        } else {
            this.cores = Integer.parseInt(cores);
        }
    }

    public void setThreads(String threads) {
        if (threads == null){
            this.threads = null;
        } else {
            this.threads = Integer.parseInt(threads);
        }
    }

    public void setBaseFrequency(String baseFrequency) {
        if (baseFrequency == null){
            this.baseFrequency = null;
        } else {
            this.baseFrequency = Integer.parseInt(baseFrequency);
        }
    }

    public void setMaxFrequency(String maxFrequency) {
        if (maxFrequency == null){
            this.maxFrequency = null;
        } else {
            this.maxFrequency = Integer.parseInt(maxFrequency);
        }
    }

    public void setTdp(String tdp) {
        if (tdp == null){
            this.tdp = null;
        } else {
            this.tdp = (int) Math.ceil(Double.parseDouble(tdp));
        }
    }

    public void setLaunchYear(String year) {
        if (year == null){
            this.launchYear = null;
        } else {
            this.launchYear = Integer.parseInt(year);
        }
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public void setNameQualifier(String nameQualifier) {
        this.nameQualifier = nameQualifier;
    }

    public void setProductCollectionQualifier(String productCollectionQualifier) {
        this.productCollectionQualifier = productCollectionQualifier;
    }

    public void setCoresQualifier(String coresQualifier) {
        this.coresQualifier = coresQualifier;
    }

    public void setThreadsQualifier(String threadsQualifier) {
        this.threadsQualifier = threadsQualifier;
    }

    public void setBaseFrequencyQualifier(String baseFrequencyQualifier) {
        this.baseFrequencyQualifier = baseFrequencyQualifier;
    }

    public void setMaxFrequencyQualifier(String maxFrequencyQualifier) {
        this.maxFrequencyQualifier = maxFrequencyQualifier;
    }

    public void setTdpQualifier(String tdpQualifier) {
        this.tdpQualifier = tdpQualifier;
    }

    public void setLaunchYearQualifier(String launchYearQualifier) {
        this.launchYearQualifier = launchYearQualifier;
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
}
