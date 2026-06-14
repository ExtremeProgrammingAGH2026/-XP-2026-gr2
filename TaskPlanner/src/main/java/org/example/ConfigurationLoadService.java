package org.example;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ConfigurationLoadService {

    private final ObjectMapper objectMapper;

    public ConfigurationLoadService() {
        this.objectMapper = new ObjectMapper();
    }

    public AppConfiguration loadConfiguration(String filePath) throws IOException {
        return objectMapper.readValue(new File(filePath), AppConfiguration.class);
    }
}
