package org.example;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class AuthService {
    private final Supplier<String> filePath;

    public AuthService(String filePath) {
        this(() -> filePath);
    }

    public AuthService(Supplier<String> filePath) {
        this.filePath = filePath;
    }

    public List<User> loadUsers() {
        String path = filePath.get();
        if (path == null || !Files.exists(Path.of(path))) {
            return new ArrayList<>();
        }
        CSVService csvService = new CSVService();
        List<List<String>> rows = csvService.readCsv(path, CsvConstants.SEPARATOR);
        List<User> users = new ArrayList<>();
        for (List<String> row : rows) {
            if (row.size() != 4) {
                throw new CsvException("Invalid user data format: " + row);
            }
            String id = row.get(0);
            String email = row.get(1);
            String name = row.get(2);
            String password = row.get(3);
            users.add(new User(id, email, name, password));
        }
        return users;
    }

    public User authenticateUser(String email, String password) {
        List<User> users = loadUsers();
        for (User user : users) {
            if (user.getEmail().equalsIgnoreCase(email) && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null; // Authentication failed
    }
}
