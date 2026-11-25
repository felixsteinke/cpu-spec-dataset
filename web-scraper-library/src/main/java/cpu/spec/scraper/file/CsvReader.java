package cpu.spec.scraper.file;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Reads csv files with one record per line (quoted and unquoted fields), e.g. a previous dataset version.
 */
public abstract class CsvReader {
    /**
     * @param file csv file with header
     * @return records as (column, value), empty if the file does not exist
     * @throws IOException if the file cannot be read
     */
    public static List<Map<String, String>> read(Path file) throws IOException {
        List<Map<String, String>> records = new ArrayList<>();
        if (!Files.exists(file)) {
            return records;
        }
        List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        if (lines.isEmpty()) {
            return records;
        }
        List<String> header = parseLine(lines.getFirst().replace("﻿", ""));
        for (String line : lines.subList(1, lines.size())) {
            if (line.isBlank()) continue;
            List<String> fields = parseLine(line);
            Map<String, String> record = new LinkedHashMap<>();
            for (int i = 0; i < header.size(); i++) {
                record.put(header.get(i), i < fields.size() ? fields.get(i) : "");
            }
            records.add(record);
        }
        return records;
    }

    /**
     * @param file csv file with header
     * @return column names, empty if the file does not exist
     * @throws IOException if the file cannot be read
     */
    public static List<String> readHeader(Path file) throws IOException {
        if (!Files.exists(file)) {
            return List.of();
        }
        try (var lines = Files.lines(file, StandardCharsets.UTF_8)) {
            return lines.findFirst().map(line -> parseLine(line.replace("﻿", ""))).orElse(List.of());
        }
    }

    static List<String> parseLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean quoted = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (quoted) {
                if (c == '"' && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    field.append('"');
                    i++;
                } else if (c == '"') {
                    quoted = false;
                } else {
                    field.append(c);
                }
            } else if (c == '"') {
                quoted = true;
            } else if (c == ',') {
                fields.add(field.toString().trim());
                field.setLength(0);
            } else {
                field.append(c);
            }
        }
        fields.add(field.toString().trim());
        return fields;
    }
}
