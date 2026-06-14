package org.example;

import java.util.ArrayList;
import java.util.List;

public class AuthService {
    private final String filePath;

    public AuthService(String filePath) {
        this.filePath = filePath;
    }

    public List<User> loadUsers() {
        CSVService csvService = new CSVService();
        List<List<String>> rows = csvService.readCsv(filePath, CsvConstants.SEPARATOR);
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
            if (user.getEmail().equals(email) && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null; // Authentication failed
    }
}