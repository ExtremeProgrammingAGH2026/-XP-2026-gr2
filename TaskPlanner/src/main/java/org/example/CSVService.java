package org.example;

import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class CSVService {

    public List<List<String>> readCsv(String path, char separator) {
        if (path == null) {
            throw new CsvException("Path cannot be null");
        }

        CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                .setDelimiter(separator)
                .build();

        List<List<String>> rows = new ArrayList<>();
        try (Reader reader = Files.newBufferedReader(Paths.get(path), StandardCharsets.UTF_8); CSVParser parser = csvFormat.parse(reader)) {
            for (CSVRecord record : parser) {
                List<String> row = new ArrayList<>(record.size());
                for (String value : record) {
                    row.add(value);
                }
                rows.add(row);
            }
            return rows;
        } catch (UncheckedIOException e) {
            Throwable cause = e.getCause() != null ? e.getCause() : e;
            throw new CsvException("Invalid CSV format: " + path, cause);
        } catch (IOException e) {
            throw new CsvException("Failed to read CSV: " + path, e);
        }
    }
}
