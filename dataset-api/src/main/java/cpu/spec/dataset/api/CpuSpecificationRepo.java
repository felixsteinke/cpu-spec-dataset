package cpu.spec.dataset.api;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CpuSpecificationRepo extends CrudRepository<CpuSpecification, String> {
    @Query("select distinct e.name from CpuSpecification e where e.name is not null")
    List<String> findDistinctNames();
}
