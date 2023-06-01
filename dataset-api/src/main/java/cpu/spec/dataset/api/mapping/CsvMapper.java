package cpu.spec.dataset.api.mapping;

import cpu.spec.dataset.api.CpuSpecification;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class CsvMapper {
    private static final Logger LOGGER = Logger.getLogger(CsvMapper.class.getName());

    /**
     * @param csvLines     raw data
     * @param csvHeaders   map with column headers
     * @param mapping      mapping from configuration
     * @param modification to parse data
     * @return mapped objects from raw csv data
     */
    public static List<CpuSpecification> mapToObjects(Stream<String> csvLines, Map<Integer, String> csvHeaders, CsvColumnIndexMapping mapping, CsvColumnModification modification) {
        return csvLines.skip(1).map((line) -> {
            String[] row = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1); // regex to notice ',' within quotes
            CpuSpecification specification = new CpuSpecification();
            try {
                Integer nIndex = getColumnIndex(row, mapping.name);
                Integer pIndex = getColumnIndex(row, mapping.productCollection);
                Integer cIndex = getColumnIndex(row, mapping.cores);
                Integer tIndex = getColumnIndex(row, mapping.threads);
                Integer bfIndex = getColumnIndex(row, mapping.baseFrequency);
                Integer mfIndex = getColumnIndex(row, mapping.maxFrequency);
                Integer tdpIndex = getColumnIndex(row, mapping.tdp);
                Integer lIndex = getColumnIndex(row, mapping.launchDate);
                Integer sIndex = getColumnIndex(row, mapping.sourceUrl);

                if (nIndex != null) {
                    specification.setName(mapValue(row[nIndex], modification.name));
                    specification.setNameQualifier(csvHeaders.get(nIndex));
                }
                if (pIndex != null) {
                    specification.setProductCollection(mapValue(row[pIndex], modification.productCollection));
                    specification.setProductCollectionQualifier(csvHeaders.get(pIndex));
                }
                if (cIndex != null) {
                    specification.setCores(mapValue(row[cIndex], modification.cores));
                    specification.setCoresQualifier(csvHeaders.get(cIndex));
                }
                if (tIndex != null) {
                    specification.setThreads(mapValue(row[tIndex], modification.threads));
                    specification.setThreadsQualifier(csvHeaders.get(tIndex));
                }
                if (bfIndex != null) {
                    specification.setBaseFrequency(mapValue(row[bfIndex], modification.baseFrequency));
                    specification.setBaseFrequencyQualifier(csvHeaders.get(bfIndex));
                }
                if (mfIndex != null) {
                    specification.setMaxFrequency(mapValue(row[mfIndex], modification.maxFrequency));
                    specification.setMaxFrequencyQualifier(csvHeaders.get(mfIndex));
                }
                if (tdpIndex != null) {
                    specification.setTdp(mapValue(row[tdpIndex], modification.tdp));
                    specification.setTdpQualifier(csvHeaders.get(tdpIndex));
                }
                if (lIndex != null) {
                    specification.setLaunchYear(mapValue(row[lIndex], modification.launchDate));
                    specification.setLaunchYearQualifier(csvHeaders.get(lIndex));
                }
                if (sIndex != null) {
                    specification.setSourceUrl(mapValue(row[sIndex], modification.sourceUrl));
                }
            } catch (Exception e) {
                LOGGER.warning("Exception in data row: " + Arrays.toString(row));
                e.printStackTrace();
            }
            return specification;
        }).collect(Collectors.toList());
    }

    private static String mapValue(String columnValue, Function<String, String> modification) {
        if (columnValue == null) {
            return null;
        }
        if (modification == null) {
            return columnValue;
        } else {
            return modification.apply(columnValue);
        }
    }

    private static Integer getColumnIndex(String[] row, int[] columnIndex) {
        if (columnIndex == null) {
            return null;
        }
        for (int index : columnIndex) {
            if (!row[index].isBlank()) {
                return index;
            }
        }
        return null;
    }
}
