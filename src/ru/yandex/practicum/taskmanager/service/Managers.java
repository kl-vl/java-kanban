package ru.yandex.practicum.taskmanager.service;

import java.nio.file.Path;

public class Managers {

    public static TaskManager getDefault(String managerType, Path filePath) {
        managerType = managerType.toLowerCase();
        return switch (managerType) {
            case "file" -> new FileBackedTaskManager(filePath);
            case "memory" -> new InMemoryTaskManager();
            default -> throw new IllegalArgumentException("Unknown manager type: " + managerType);
        };
    }

    public static TaskManager getDefault(String managerType) {
        managerType = managerType.toLowerCase();
        if ("file".equals(managerType)) {
            throw new IllegalArgumentException("Failed to initialize FileBackedTaskManager: Path is required for 'file' manager type.");
        }
        return getDefault(managerType, null);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

}
