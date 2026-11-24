package cpu.spec.dataset.api.mapping;

import cpu.spec.dataset.api.CpuSpecification;
import cpu.spec.dataset.api.dataset.Dataset;

import java.io.IOException;
import java.util.Arrays;
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
            String[] row = Arrays.stream(line.split(Dataset.CSV_SPLIT_REGEX, -1))
                    .map(CsvMapper::unquote)
                    .toArray(String[]::new);
            CpuSpecification specification = new CpuSpecification(row, mapping);
            specification.updateValues(row, modification);
            specification.updateQualifier(columns);
            return specification;
        }).collect(Collectors.toList());
    }

    /**
     * @param cell raw csv cell (example: "16" or "a ""quoted"" value")
     * @return trimmed cell without surrounding quotes and with escaped quotes resolved
     */
    static String unquote(String cell) {
        String value = cell.trim();
        if (value.length() >= 2 && value.startsWith("\"") && value.endsWith("\"")) {
            value = value.substring(1, value.length() - 1).replace("\"\"", "\"");
        }
        return value;
    }
}
