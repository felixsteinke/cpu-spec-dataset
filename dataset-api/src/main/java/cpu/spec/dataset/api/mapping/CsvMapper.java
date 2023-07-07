package cpu.spec.dataset.api.mapping;

import cpu.spec.dataset.api.CpuSpecification;
import cpu.spec.dataset.api.registry.Dataset;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class CsvMapper {
    /**
     * @param dataset raw data
     * @return mapped objects from raw csv data
     */
    public static List<CpuSpecification> mapToObjects(Dataset dataset) throws IOException {
        Stream<String> lines = dataset.getDatasetLines();
        Map<Integer, String> columns = dataset.getDatasetColumns();
        CsvColumnIndexMapping mapping = dataset.getColumnMapping();
        CsvColumnModification modification = dataset.getColumnModifications();
        return lines.map((line) -> {
            String[] row = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1); // regex to notice ',' within quotes
            CpuSpecification specification = new CpuSpecification(row, mapping);
            specification.updateValues(row, modification);
            specification.updateQualifier(columns);
            return specification;
        }).collect(Collectors.toList());
    }
}
