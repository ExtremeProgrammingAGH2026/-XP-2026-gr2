package org.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class ConfigurationSaveTest {

    @TempDir
    Path tempDir;

    @Test
    public void testSaveFullConfigurationToJson() throws IOException {
        // Arrange
        File configFile = tempDir.resolve("config.json").toFile();
        AppConfiguration config = new AppConfiguration();
        config.setUsersFilePath("data/users.csv");
        config.setTasksFilePath("data/tasks.csv");
        config.setMaxLoginAttempts(3);
        config.setMinPasswordLength(8);
        config.setTimeZone("Europe/Warsaw");
        config.setDateTimeFormat("dd.MM.yyyy HH:mm");

        ConfigurationSaveService service = new ConfigurationSaveService();

        // Act
        assertDoesNotThrow(() -> service.saveConfiguration(config, configFile.getAbsolutePath()));

        // Assert
        assertTrue(configFile.exists(), "Configuration file should be created");
        
        String jsonContent = Files.readString(configFile.toPath());
        
        assertTrue(jsonContent.contains("\"usersFilePath\""), "JSON should contain usersFilePath");
        assertTrue(jsonContent.contains("\"data/users.csv\""), "JSON should contain usersFilePath value");
        
        assertTrue(jsonContent.contains("\"tasksFilePath\""), "JSON should contain tasksFilePath");
        assertTrue(jsonContent.contains("\"data/tasks.csv\""), "JSON should contain tasksFilePath value");
        
        assertTrue(jsonContent.contains("\"maxLoginAttempts\""), "JSON should contain maxLoginAttempts");
        assertTrue(jsonContent.contains("3"), "JSON should contain maxLoginAttempts value");
        
        assertTrue(jsonContent.contains("\"minPasswordLength\""), "JSON should contain minPasswordLength");
        assertTrue(jsonContent.contains("8"), "JSON should contain minPasswordLength value");
        
        assertTrue(jsonContent.contains("\"timeZone\""), "JSON should contain timeZone");
        assertTrue(jsonContent.contains("\"Europe/Warsaw\""), "JSON should contain timeZone value");
        
        assertTrue(jsonContent.contains("\"dateTimeFormat\""), "JSON should contain dateTimeFormat");
        assertTrue(jsonContent.contains("\"dd.MM.yyyy HH:mm\""), "JSON should contain dateTimeFormat value");
    }

    @Test
    public void testSaveConfigurationWithNullValues() throws IOException {
        // Arrange
        File configFile = tempDir.resolve("config_null.json").toFile();
        AppConfiguration config = new AppConfiguration(); // Empty config
        
        ConfigurationSaveService service = new ConfigurationSaveService();

        // Act
        assertDoesNotThrow(() -> service.saveConfiguration(config, configFile.getAbsolutePath()));

        // Assert
        assertTrue(configFile.exists(), "Configuration file should be created even with null values");
    }
}
