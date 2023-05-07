package cpu.spec.dataset.api;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
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
    private String cores;
    @Column(name = "threads")
    private String threads;
    @Column(name = "base_frequency")
    private String baseFrequency;
    @Column(name = "max_frequency")
    private String maxFrequency;
    @Column(name = "tdp")
    private String tdp;
    @Column(name = "launch_date")
    private String launchDate;
    @Column(name = "max_ram")
    private String maxRam;
}
