package demo;

import cpu.spec.dataset.api.file.ResourceReader;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ColumnPrinter {
    public static void main(String[] args) throws IOException {
        Map<Integer, String> intelColumns = getColumnMap(
                ResourceReader.getIntelDataset()
                        .findFirst().orElseThrow());
        Map<Integer, String> amdColumns = getColumnMap(
                ResourceReader.getAmdDataset()
                        .findFirst().orElseThrow());
        System.out.println("Intel: " + intelColumns);
        System.out.println("AMD: " + amdColumns);
    }

    private static Map<Integer, String> getColumnMap(String headerLine) {
        Map<Integer, String> map = new HashMap<>();
        String[] split = headerLine.split(",");
        for (int i = 0; i < split.length; i++) {
            map.put(i, split[i]);
        }
        return map;

    }
}
