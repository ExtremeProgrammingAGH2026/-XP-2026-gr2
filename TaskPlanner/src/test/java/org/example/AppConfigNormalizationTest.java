package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AppConfigNormalizationTest {

    @Test
    void fillsDefaultsForMissingValues() {
        AppConfiguration normalized = App.normalize(new AppConfiguration()); // all null / 0

        assertEquals("data/users.csv", normalized.getUsersFilePath());
        assertEquals("data/tasks.csv", normalized.getTasksFilePath());
        assertEquals(3, normalized.getMaxLoginAttempts());
        assertEquals(8, normalized.getMinPasswordLength());
        assertEquals("Europe/Warsaw", normalized.getTimeZoneName());
        assertEquals("dd.MM.yyyy HH:mm", normalized.getDateTimeFormat());
    }

    @Test
    void replacesInvalidValuesWithDefaults() {
        AppConfiguration broken = new AppConfiguration();
        broken.setUsersFilePath("custom/users.csv");
        broken.setTasksFilePath("custom/tasks.csv");
        broken.setMaxLoginAttempts(0);
        broken.setMinPasswordLength(0);
        broken.setTimeZoneName("Mars/Phobos");
        broken.setDateTimeFormat("zzz{{{");

        AppConfiguration normalized = App.normalize(broken);

        assertEquals(3, normalized.getMaxLoginAttempts());
        assertEquals(8, normalized.getMinPasswordLength());
        assertEquals("Europe/Warsaw", normalized.getTimeZoneName());
        assertEquals("dd.MM.yyyy HH:mm", normalized.getDateTimeFormat());
        // valid values are preserved
        assertEquals("custom/users.csv", normalized.getUsersFilePath());
        assertEquals("custom/tasks.csv", normalized.getTasksFilePath());
    }
}
