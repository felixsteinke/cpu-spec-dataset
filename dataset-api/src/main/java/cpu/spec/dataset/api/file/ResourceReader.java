package cpu.spec.dataset.api.file;

import com.fasterxml.jackson.databind.ObjectMapper;
import cpu.spec.dataset.api.mapping.CsvColumnIndexMapping;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class ResourceReader {
    private static final ObjectMapper OBJ_MAPPER = new ObjectMapper();
    public static final String INTEL_MAPPING = "mapping/intel-csv-mapping.json";
    public static final String AMD_MAPPING = "mapping/amd-csv-mapping.json";
    public static final String INTEL_DATASET_CSV = "dataset/intel-cpus.csv";
    public static final String AMD_DATASET_CSV = "dataset/amd-cpus.csv";

    /**
     * @param resourceFilePath resource file path
     * @return raw csv file lines
     * @throws IOException if input stream is null
     */
    public static Stream<String> getCsvLines(String resourceFilePath) throws IOException {
        return getResourceReader(resourceFilePath).lines();
    }

    /**
     * @return mapped object from the file
     * @throws IOException if input stream is null
     */
    public static CsvColumnIndexMapping getCsvMapping(String resourceFilePath) throws IOException {
        try (BufferedReader reader = getResourceReader(resourceFilePath)) {
            String json = reader.lines().collect(Collectors.joining());
            return OBJ_MAPPER.readValue(json, CsvColumnIndexMapping.class);
        }
    }

    /**
     * Note: Don't forget to close the reader.
     *
     * @param resourcePath path within the resource directory (example: directory/file.txt)
     * @return reader of input stream
     * @throws IOException if input stream is null
     */
    private static BufferedReader getResourceReader(String resourcePath) throws IOException {
        InputStream inputStream = ResourceReader.class.getClassLoader().getResourceAsStream(resourcePath);
        if (inputStream == null)
            throw new IOException("No content at " + resourcePath);

        return new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
    }
}
