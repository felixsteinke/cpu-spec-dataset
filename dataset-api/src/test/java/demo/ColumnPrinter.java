package demo;

import cpu.spec.dataset.api.file.ResourceReader;

import java.io.IOException;
import java.util.Map;

public class ColumnPrinter {
    public static void main(String[] args) throws IOException {
        Map<Integer, String> intelColumns = ResourceReader.getIntelColumns();
        Map<Integer, String> amdColumns = ResourceReader.getAmdColumns();
        Map<Integer, String> benchmarkColumns = ResourceReader.getCpuBenchmarkColumns();
        System.out.println("Intel: " + intelColumns);
        System.out.println("AMD: " + amdColumns);
        System.out.println("CPU Benchmark: " + benchmarkColumns);
    }
}
