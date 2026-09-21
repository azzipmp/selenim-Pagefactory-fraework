package tests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CsvUtils {

    private CsvUtils() {
    }

    public static CsvTable readCsvData(String resourcePath) {
        List<Map<String, String>> rows = new ArrayList<>();

        try (InputStream inputStream = CsvUtils.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new IllegalStateException("CSV resource not found: " + resourcePath);
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                String headerLine = reader.readLine();
                if (headerLine == null || headerLine.trim().isEmpty()) {
                    throw new IllegalStateException("CSV header is missing in: " + resourcePath);
                }

                String[] headers = parseCsvLine(headerLine);
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.trim().isEmpty() || line.trim().startsWith("#")) {
                        continue;
                    }

                    String[] values = parseCsvLine(line);
                    Map<String, String> row = new HashMap<>();
                    for (int index = 0; index < headers.length; index++) {
                        String header = headers[index].trim().toLowerCase();
                        String value = index < values.length ? values[index].trim() : "";
                        row.put(header, value);
                    }
                    rows.add(row);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read CSV resource: " + resourcePath, e);
        }

        if (rows.isEmpty()) {
            throw new AssertionError("No valid test data rows found in: " + resourcePath);
        }

        return new CsvTable(rows, resourcePath);
    }

    private static String[] parseCsvLine(String line) {
        return line.split(",", -1);
    }

    public static final class CsvTable {
        private final List<Map<String, String>> rows;
        private final String source;

        private CsvTable(List<Map<String, String>> rows, String source) {
            this.rows = rows;
            this.source = source;
        }

        public int rowCount() {
            return rows.size();
        }

        public String source() {
            return source;
        }

        public String getValue(int rowIndex, String columnName) {
            if (rowIndex < 0 || rowIndex >= rows.size()) {
                throw new IndexOutOfBoundsException("Row index out of bounds: " + rowIndex);
            }
            if (columnName == null || columnName.isBlank()) {
                throw new IllegalArgumentException("Column name must not be blank");
            }
            return rows.get(rowIndex).get(columnName.trim().toLowerCase());
        }
    }
}
