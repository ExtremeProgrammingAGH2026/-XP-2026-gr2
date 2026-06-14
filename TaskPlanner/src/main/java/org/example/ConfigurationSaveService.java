package org.example;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ConfigurationSaveService {

    private final ObjectMapper objectMapper;

    public ConfigurationSaveService() {
        this.objectMapper = new ObjectMapper();

    }

    public void saveConfiguration(AppConfiguration config, String filePath) throws IOException {

    }
}
