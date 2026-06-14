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
        return objectMapper.readValue(new File(filePath), AppConfiguration.class); // This will throw an IOException if the file is not found or cannot be read
    }
}
