package org.example;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class ConfigurationSaveService {

    private final ObjectMapper objectMapper;

    public ConfigurationSaveService() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public void saveConfiguration(AppConfiguration config, String filePath) throws IOException {
        objectMapper.writeValue(new File(filePath), config);
    }
}
