package cpu.spec.dataset.api.file;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

public abstract class ResourceReader {
    public static final String INTEL_DATASET_CSV = "dataset/intel-cpus.csv";
    public static final String AMD_DATASET_CSV = "dataset/amd-cpus.csv";

    /**
     * @return csv lines of AMD dataset
     * @throws IOException if input stream is null
     */
    public static Stream<String> getAmdDataset() throws IOException {
        return getCsvLines(AMD_DATASET_CSV);
    }

    /**
     * @return csv lines of intel dataset
     * @throws IOException if input stream is null
     */
    public static Stream<String> getIntelDataset() throws IOException {
        return getCsvLines(INTEL_DATASET_CSV);
    }

    /**
     * @param resourceFilePath resource file path
     * @return raw csv file lines
     * @throws IOException if input stream is null
     */
    private static Stream<String> getCsvLines(String resourceFilePath) throws IOException {
        return getResourceReader(resourceFilePath).lines();
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
