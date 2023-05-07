package cpu.spec.dataset.api.file;

import com.fasterxml.jackson.databind.ObjectMapper;
import cpu.spec.dataset.api.database.CsvColumnMapping;
import cpu.spec.dataset.api.database.CsvToDbImporter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

public abstract class ResourceReader {
    private static final ObjectMapper OBJ_MAPPER = new ObjectMapper();

    public static List<CsvColumnMapping> getAmdCsvMapping() throws IOException {
        String json = getResourceReader("mapping/amd-csv-mapping.json").lines().collect(Collectors.joining());
        return OBJ_MAPPER.readValue(json, OBJ_MAPPER.getTypeFactory().constructCollectionType(List.class, CsvColumnMapping.class));
    }

    public static List<CsvColumnMapping> getIntelCsvMapping() throws IOException {
        String json = getResourceReader("mapping/intel-csv-mapping.json").lines().collect(Collectors.joining());
        return OBJ_MAPPER.readValue(json, OBJ_MAPPER.getTypeFactory().constructCollectionType(List.class, CsvColumnMapping.class));
    }

    /**
     * Note: Don't forget to close the reader.
     *
     * @param resourcePath path within the resource directory (example: directory/file.txt)
     * @return reader of input stream
     * @throws IOException if input stream is null
     */
    private static BufferedReader getResourceReader(String resourcePath) throws IOException {
        InputStream inputStream = CsvToDbImporter.class.getClassLoader().getResourceAsStream(resourcePath);
        if (inputStream == null)
            throw new IOException("No content at " + resourcePath);

        return new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
    }
}
