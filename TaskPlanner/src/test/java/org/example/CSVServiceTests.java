package org.example;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class CSVServiceTests {

    public CSVService csvService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        csvService = new CSVService();
    }

    @Test
    public void shouldReadCsvWhenFileExists() {
        Path csvPath = tempDir.resolve("simple.csv");
        writeUtf8(csvPath, "a;b\nc;d\n");

        List<List<String>> rows = csvService.readCsv(csvPath.toString(), ';');

        assertEquals(2, rows.size());
        List<String> expected = new java.util.ArrayList<>();
        expected.add("a");
        expected.add("b");
        assertIterableEquals(expected, rows.get(0));
        List<String> expected1 = new java.util.ArrayList<>();
        expected1.add("c");
        expected1.add("d");
        assertIterableEquals(expected1, rows.get(1));
    }

    @Test
    public void shouldThrowExceptionWhenFileDoesNotExist() {
        Path missingPath = tempDir.resolve("missing.csv");

        assertThrows(CsvException.class, () -> csvService.readCsv(missingPath.toString(), ';'));
    }

    @Test
    public void shouldThrowExceptionWhenInvalidCsvFormat() {
        Path csvPath = tempDir.resolve("invalid.csv");
        // Unclosed quoted value should be treated as invalid CSV.
        writeUtf8(csvPath, "a;\"b\n");

        assertThrows(CsvException.class, () -> csvService.readCsv(csvPath.toString(), ';'));
    }

    @Test
    public void shouldReadCsvWhenContainsSpecialCharacters() {
        Path csvPath = tempDir.resolve("utf8.csv");
        writeUtf8(csvPath, "Zażółć;gęślą;jaźń\nąćęłńóśźż;€;漢字\n");

        List<List<String>> rows = csvService.readCsv(csvPath.toString(), ';');

        assertEquals(2, rows.size());
        List<String> expected = new java.util.ArrayList<>();
        expected.add("Zażółć");
        expected.add("gęślą");
        expected.add("jaźń");
        assertIterableEquals(expected, rows.get(0));
        List<String> expected1 = new java.util.ArrayList<>();
        expected1.add("ąćęłńóśźż");
        expected1.add("€");
        expected1.add("漢字");
        assertIterableEquals(expected1, rows.get(1));
    }

    @Test
    public void shouldReadCsvWhenFileEmpty() {
        Path csvPath = tempDir.resolve("empty.csv");
        writeUtf8(csvPath, "");

        List<List<String>> rows = csvService.readCsv(csvPath.toString(), ';');

        assertNotNull(rows);
        assertTrue(rows.isEmpty());
    }

    private static void writeUtf8(Path path, String content) {
        try {
            Files.write(path, content.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            fail("Failed to write test CSV file: " + e.getMessage());
        }
    }
}
