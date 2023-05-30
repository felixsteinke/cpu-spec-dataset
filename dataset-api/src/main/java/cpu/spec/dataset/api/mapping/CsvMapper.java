package cpu.spec.dataset.api.mapping;

import cpu.spec.dataset.api.CpuSpecification;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class CsvMapper {
    /**
     * @param csvLines     raw data
     * @param mapping      mapping from configuration
     * @param modification to parse data
     * @return mapped objects from raw csv data
     */
    public static List<CpuSpecification> mapToObjects(Stream<String> csvLines, CsvColumnIndexMapping mapping, CsvColumnModification modification) {
        return csvLines.skip(1).map((line) -> {
            String[] row = line.split(",");

            CpuSpecification specification = new CpuSpecification();
            specification.setName(getValue(row, mapping.name, modification.name));
            specification.setManufacturer(getValue(row, mapping.manufacturer, modification.manufacturer));
            specification.setProductCollection(getValue(row, mapping.productCollection, modification.productCollection));
            specification.setCores(getValue(row, mapping.cores, modification.cores));
            specification.setThreads(getValue(row, mapping.threads, modification.threads));
            specification.setBaseFrequency(getValue(row, mapping.baseFrequency, modification.baseFrequency));
            specification.setMaxFrequency(getValue(row, mapping.maxFrequency, modification.maxFrequency));
            specification.setTdp(getValue(row, mapping.tdp, modification.tdp));
            specification.setLaunchDate(getDateValue(row, mapping.launchDate));
            return specification;
        }).collect(Collectors.toList());
    }

    private static String getValue(String[] row, int[] columnIndex, Function<String, String> modification) {
        if (columnIndex == null) {
            return null;
        }
        String value;
        for (int index : columnIndex) {
            value = row[index];
            if (!value.isBlank()) {
                if (modification == null) {
                    return value;
                } else {
                    return modification.apply(value);
                }
            }
        }
        return null;
    }

    private static LocalDate getDateValue(String[] row, int[] columnIndex) {
        if (columnIndex == null) {
            return null;
        }
        return null;
    }
}
