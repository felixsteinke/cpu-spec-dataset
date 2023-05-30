package cpu.spec.dataset.api;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@EqualsAndHashCode
@NoArgsConstructor
@Entity(name = "CpuSpecification")
@Table(name = "cpu_specifications")
public class CpuSpecification {
    @Id
    @Column(name = "name")
    private String name;
    @Column(name = "manufacturer")
    private String manufacturer;
    @Column(name = "product_collection")
    private String productCollection;
    @Column(name = "cores")
    private Integer cores;
    @Column(name = "threads")
    private Integer threads;
    @Column(name = "base_frequency")
    private Integer baseFrequency;
    @Column(name = "max_frequency")
    private Integer maxFrequency;
    @Column(name = "tdp")
    private Integer tdp;
    @Column(name = "launch_date")
    private LocalDate launchDate;
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
        this.cores = Integer.parseInt(cores);
    }

    public void setThreads(String threads) {
        this.threads = Integer.parseInt(threads);
    }

    public void setBaseFrequency(String baseFrequency) {
        this.baseFrequency = Integer.parseInt(baseFrequency);
    }

    public void setMaxFrequency(String maxFrequency) {
        this.maxFrequency = Integer.parseInt(maxFrequency);
    }

    public void setTdp(String tdp) {
        this.tdp = Integer.parseInt(tdp);
    }

    public void setLaunchDate(LocalDate launchDate) {
        this.launchDate = launchDate;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }
}
