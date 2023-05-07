package cpu.spec.dataset.api.database;

import cpu.spec.dataset.api.CpuSpecification;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CpuSpecificationRepo extends CrudRepository<CpuSpecification, String> {
}
