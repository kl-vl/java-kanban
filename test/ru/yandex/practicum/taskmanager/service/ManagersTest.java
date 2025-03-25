package ru.yandex.practicum.taskmanager.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ManagersTest {

    @Test
    void getDefault_shouldReturnNonNullTaskManagerInstanceForAllTypes(@TempDir Path tempDir) throws IOException {
        Path tempFile = Files.createTempFile(tempDir,"tasks", ".csv");
        final TaskManager taskManager1 = Managers.getDefault("memory");
        final TaskManager taskManager2 = Managers.getDefault("file", tempFile);
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Managers.getDefault("file")
        );

        assertAll("Managers should be correctly initialized by type",
                () -> assertNotNull(taskManager1, "TaskManager type 'memory' should not be null"),
                () -> assertNotNull(taskManager2, "TaskManager type 'file' should not be null"),
                () -> assertTrue(exception.getMessage().contains("Failed to initialize FileBackedTaskManager: Path is required for 'file' manager type."))
        );
    }

    @Test
    void getDefaultHistory_shouldReturnNonNullHistoryManagerInstance() {
        HistoryManager historyManager = Managers.getDefaultHistory();

        assertNotNull(historyManager, "HistoryManager should not be null");
    }

}