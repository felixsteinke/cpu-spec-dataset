package cpu.spec.scraper.file;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DatasetMergeTest {
    @TempDir
    Path dir;

    @Test
    void recordsUpdateAddAndKeepRows() throws Exception {
        Path dataset = csv("dataset.csv", "CpuName,Cores,SourceUrl", "A,2,a", "B,4,b", "C,8,c");
        Path records = csv("records.csv", "CpuName,Cores,Threads,SourceUrl", "C,16,32,c2", "D,6,12,d", "A,2,4,a");

        DatasetMerge.Result result = DatasetMerge.merge(records, dataset);

        assertEquals(new DatasetMerge.Result(2, 1, 1), result);
        // columns of the dataset keep their order, new columns are appended
        assertEquals(List.of("CpuName", "Cores", "SourceUrl", "Threads"), CsvReader.readHeader(dataset));
        // rows of the dataset keep their order (updated or kept), new CPUs are appended
        assertEquals(List.of(
                Map.of("CpuName", "A", "Cores", "2", "SourceUrl", "a", "Threads", "4"),
                Map.of("CpuName", "B", "Cores", "4", "SourceUrl", "b", "Threads", ""),
                Map.of("CpuName", "C", "Cores", "16", "SourceUrl", "c2", "Threads", "32"),
                Map.of("CpuName", "D", "Cores", "6", "SourceUrl", "d", "Threads", "12")), CsvReader.read(dataset));
    }

    @Test
    void updatedRowsAreReplacedByTheRecord() throws Exception {
        Path dataset = csv("dataset.csv", "CpuName,Secure Key", "A,Yes");
        Path records = csv("records.csv", "CpuName,Intel® Secure Key", "A,Yes");

        DatasetMerge.merge(records, dataset);

        assertEquals(List.of(Map.of("CpuName", "A", "Secure Key", "", "Intel® Secure Key", "Yes")), CsvReader.read(dataset));
    }

    @Test
    void cpusWithTheSameNameAreIdentifiedByCpuId() throws Exception {
        Path dataset = csv("dataset.csv", "CpuId,\"CpuName\",TDP", "1,X,10 W", "2,X,20 W");
        Path records = csv("records.csv", "CpuId,\"CpuName\",TDP", "2,X,25 W", "3,X,30 W");

        assertEquals(new DatasetMerge.Result(1, 1, 1), DatasetMerge.merge(records, dataset));
        assertEquals(List.of("10 W", "25 W", "30 W"), CsvReader.read(dataset).stream().map(row -> row.get("TDP")).toList());
    }

    @Test
    void amdDatasetIsIdentifiedByName() throws Exception {
        Path dataset = csv("dataset.csv", "\"Name\",\"Family\"", "\"Ryzen 5\",\"Ryzen\"");
        Path records = csv("records.csv", "\"Name\",\"Family\"", "\"Ryzen 5\",\"Ryzen 5\"");

        assertEquals(new DatasetMerge.Result(1, 0, 0), DatasetMerge.merge(records, dataset));
    }

    @Test
    void renamedCpuKeepsItsPreviousRow() throws Exception {
        // the old name stays an id of the Dataset API
        Path dataset = csv("dataset.csv", "CpuId,CpuName", "1,Old Name");
        Path records = csv("records.csv", "CpuId,CpuName", "1,New Name");

        assertEquals(new DatasetMerge.Result(0, 1, 1), DatasetMerge.merge(records, dataset));
    }

    @Test
    void replaceWritesOnlyTheRecords() throws Exception {
        Path dataset = csv("dataset.csv", "CpuName,Cores", "A,2", "B,4");
        Path records = csv("records.csv", "CpuName,Threads", "C,8");

        assertEquals(new DatasetMerge.Result(0, 1, 0), DatasetMerge.replace(records, dataset));
        assertEquals(List.of(Map.of("CpuName", "C", "Threads", "8")), CsvReader.read(dataset));
    }

    @Test
    void newDatasetGetsAllRecords() throws Exception {
        Path records = csv("records.csv", "CpuName", "A", "B");

        assertEquals(new DatasetMerge.Result(0, 2, 0), DatasetMerge.merge(records, dir.resolve("new.csv")));
    }

    @Test
    void filesWithoutCommonNameColumnAreRejected() throws Exception {
        Path dataset = csv("dataset.csv", "CpuName", "A");
        Path records = csv("records.csv", "Model", "A");

        assertThrows(IllegalArgumentException.class, () -> DatasetMerge.merge(records, dataset));
        assertEquals(List.of(Map.of("CpuName", "A")), CsvReader.read(dataset), "dataset unchanged");
    }

    private Path csv(String name, String... lines) throws Exception {
        return Files.writeString(dir.resolve(name), String.join("\n", lines) + "\n");
    }
}
