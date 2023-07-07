package cpu.spec.dataset.api.registry;

import cpu.spec.dataset.api.file.ResourceReader;
import cpu.spec.dataset.api.mapping.CsvColumnIndexMapping;
import cpu.spec.dataset.api.mapping.CsvColumnModification;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

public abstract class Dataset {
    private final String DATASET_PATH;

    /**
     * @param datasetResourcePath resource file path
     */
    protected Dataset(String datasetResourcePath) {
        DATASET_PATH = datasetResourcePath;
    }

    /**
     * @return name of the dataset extracted of the path
     */
    public String getName() {
        String[] pathSplit = DATASET_PATH.split("/");
        if (pathSplit.length > 0) {
            return pathSplit[pathSplit.length - 1];
        } else {
            return "Unknown Dataset";
        }
    }

    /**
     * @return column mapping with fallbacks for cpu data (readonly)
     */
    public abstract CsvColumnIndexMapping getColumnMapping();

    /**
     * @return modifications for cpu data (readonly)
     */
    public abstract CsvColumnModification getColumnModifications();

    /**
     * @return raw csv lines without header
     * @throws IOException if no resource at dataset path can be found
     */
    public Stream<String> getDatasetLines() throws IOException {
        return ResourceReader.getResourceReader(DATASET_PATH).lines().skip(1);
    }

    /**
     * @return map of (columnIndex, csvHeader)
     * @throws IOException            if no resource at dataset path can be found
     * @throws NoSuchElementException if csv lines are empty
     */
    public Map<Integer, String> getDatasetColumns() throws IOException, NoSuchElementException {
        Map<Integer, String> map = new HashMap<>();
        String[] split = ResourceReader.getResourceReader(DATASET_PATH).lines().findFirst().orElseThrow().split(",");
        for (int i = 0; i < split.length; i++) {
            map.put(i, split[i]);
        }
        return map;
    }
}
