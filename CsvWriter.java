package io;

import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;

public class CsvWriter {
    public void write(String path, java.util.List<String[]> rows) throws Exception {
        Path p = Path.of(path).getParent();
        if (p != null && !Files.exists(p)) {
            Files.createDirectories(p);
        }
        try (CSVWriter w = new CSVWriter(new FileWriter(path))) {
            for (String[] r : rows) {
                w.writeNext(r);
            }
        }
    }
}
