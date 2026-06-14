package org.example;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ConfigurationLoadService {

    private final ObjectMapper objectMapper;

    public ConfigurationLoadService() {
        this.objectMapper = new ObjectMapper(); // Default configuration is sufficient for our needs
    }

    public AppConfiguration loadConfiguration(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            AppConfiguration defaultConfig = new AppConfiguration();
            defaultConfig.setUsersFilePath("data/users.csv");
            defaultConfig.setTasksFilePath("data/tasks.csv");
            defaultConfig.setMaxLoginAttempts(3);
            defaultConfig.setMinPasswordLength(8);
            defaultConfig.setTimeZoneName("Europe/Warsaw");
            defaultConfig.setDateTimeFormat("dd.MM.yyyy HH:mm");
            return defaultConfig;
        }
        return objectMapper.readValue(file, AppConfiguration.class);
    }
}
