package cpu.spec.scraper.file;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Writes RFC 4180 style csv files: every field is quoted, quotes are escaped as "" and line breaks are
 * replaced by spaces, so each record is exactly one line.
 */
public abstract class CsvWriter {
    /**
     * @param file   target file
     * @param header column names
     * @param rows   records with the same column order as the header (missing values as null)
     * @throws IOException if the file cannot be written
     */
    public static void write(Path file, List<String> header, List<List<String>> rows) throws IOException {
        try (Writer writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            writer.write(toLine(header));
            for (List<String> row : rows) {
                writer.write(toLine(row));
            }
        }
    }

    static String toLine(List<String> fields) {
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < fields.size(); i++) {
            if (i > 0) {
                line.append(',');
            }
            line.append(quote(fields.get(i)));
        }
        return line.append('\n').toString();
    }

    static String quote(String value) {
        if (value == null) {
            return "\"\"";
        }
        String clean = value.replaceAll("[\\r\\n]+", " ").trim().replace("\"", "\"\"");
        return "\"" + clean + "\"";
    }
}
