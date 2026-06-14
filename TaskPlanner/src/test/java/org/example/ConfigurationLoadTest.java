package org.example;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.*;

public class ConfigurationLoadTest {

    @TempDir
    Path tempDir;

    @Test
    public void testLoadFullConfigurationFromJson() throws IOException { // This test verifies that a fully populated JSON configuration file is correctly loaded into an AppConfiguration object
        // Arrange
        File configFile = tempDir.resolve("config.json").toFile();
        String json = "{\n" +
                "  \"usersFilePath\": \"data/users.csv\",\n" +
                "  \"tasksFilePath\": \"data/tasks.csv\",\n" +
                "  \"maxLoginAttempts\": 3,\n" +
                "  \"minPasswordLength\": 8,\n" +
                "  \"timeZoneName\": \"Europe/Warsaw\",\n" +
                "  \"dateTimeFormat\": \"dd.MM.yyyy HH:mm\"\n" +
                "}";
        Files.writeString(configFile.toPath(), json);

        ConfigurationLoadService service = new ConfigurationLoadService();

        // Act
        AppConfiguration config = service.loadConfiguration(configFile.getAbsolutePath());

        // Assert
        assertNotNull(config, "Loaded configuration should not be null");
        assertEquals("data/users.csv", config.getUsersFilePath());
        assertEquals("data/tasks.csv", config.getTasksFilePath());
        assertEquals(3, config.getMaxLoginAttempts());
        assertEquals(8, config.getMinPasswordLength());
        assertEquals("Europe/Warsaw", config.getTimeZoneName());
        assertEquals("dd.MM.yyyy HH:mm", config.getDateTimeFormat());
    }

    @Test
    public void testLoadConfigurationWithMissingFields() throws IOException { // This test checks that when a JSON configuration file is missing some fields, the loadConfiguration method still returns a valid AppConfiguration object with default values for the missing fields
        // Arrange: JSON missing most fields -> defaults (ints default to 0, strings to null)
        File configFile = tempDir.resolve("config_partial.json").toFile();
        String json = "{\n" +
                "  \"usersFilePath\": null,\n" +
                "  \"tasksFilePath\": null\n" +
                "}";
        Files.writeString(configFile.toPath(), json);

        ConfigurationLoadService service = new ConfigurationLoadService();

        // Act
        AppConfiguration config = service.loadConfiguration(configFile.getAbsolutePath());

        // Assert
        assertNotNull(config, "Loaded configuration should not be null even with missing fields");
        assertNull(config.getUsersFilePath());
        assertNull(config.getTasksFilePath());
        // primitive ints default to 0 when not present in JSON
        assertEquals(0, config.getMaxLoginAttempts());
        assertEquals(0, config.getMinPasswordLength());
        assertNull(config.getTimeZoneName());
        assertNull(config.getDateTimeFormat());
    }

    @Test
    public void testLoadInvalidJsonThrows() throws IOException { // This test ensures that if the JSON configuration file is malformed, the loadConfiguration method throws an IOException, indicating that the file could not be read or parsed correctly
        // Arrange
        File configFile = tempDir.resolve("config_invalid.json").toFile();
        String json = "{ invalid json }";
        Files.writeString(configFile.toPath(), json);

        ConfigurationLoadService service = new ConfigurationLoadService();

        // Act & Assert
        assertThrows(IOException.class, () -> service.loadConfiguration(configFile.getAbsolutePath()));
    }
}
