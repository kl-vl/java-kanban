package ru.yandex.practicum.taskmanager.service;

import ru.yandex.practicum.taskmanager.model.Epic;
import ru.yandex.practicum.taskmanager.model.Status;
import ru.yandex.practicum.taskmanager.model.Subtask;
import ru.yandex.practicum.taskmanager.model.Task;
import ru.yandex.practicum.taskmanager.model.Type;

public class TaskDeserializer {

    private static final int CSV_FIELD_COUNT = 6;
    private static final TaskDeserializer instance = new TaskDeserializer();

    private TaskDeserializer() {
    }

    public static TaskDeserializer getInstance() {
        return instance;
    }

    public int getCsvFieldCount() {
        return CSV_FIELD_COUNT;
    }

    public static Task deserialize(String csvLine, TaskManager taskManager) {
        String[] fields = parseCsvLine(csvLine);
        Type type = parseType(fields[1]);
        return switch (type) {
            case TASK -> createTask(fields);
            case SUBTASK -> createSubtask(fields, taskManager);
            case EPIC -> createEpic(fields);
        };
    }

    public static Task deserialize(String csvLine) {
        return deserialize(csvLine, null);
    }

    private static String[] parseCsvLine(String csvLine) {
        String[] fields = csvLine.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", CSV_FIELD_COUNT);
        if (fields.length != CSV_FIELD_COUNT) {
            throw new IllegalArgumentException("Invalid CSV format: " + csvLine);
        }
        for (int i = 0; i < fields.length; i++) {
            fields[i] = unescapeCsv(fields[i]);
        }
        return fields;
    }

    public static String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            value = value.replace("\"", "\"\"");
            value = "\"" + value + "\"";
        }
        return value;
    }

    public static String unescapeCsv(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }
        if (value.startsWith("\"") && value.endsWith("\"")) {
            value = value.substring(1, value.length() - 1);
            value = value.replace("\"\"", "\"");
        }
        return value;
    }

    private static Type parseType(String typeField) {
        try {
            return Type.valueOf(unescapeCsv(typeField));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(String.format("Unknown task type '%s' in CSV line", typeField), e);
        }
    }

    private static Epic resolveEpic(int epicId, TaskManager manager) {
        if (manager == null) {
            throw new IllegalStateException("TaskManager is required to resolve epic reference");
        }
        return manager.getEpicById(epicId).orElseThrow(() -> new IllegalArgumentException(String.format("Epic ID=%d not found in Task manager", epicId)));
    }

    private static Task createTask(String[] fields) {
        return Task.createForDeserialization(Integer.parseInt(fields[0]), fields[2], fields[4], Status.valueOf(fields[3]));
    }

    private static Subtask createSubtask(String[] fields, TaskManager taskManager) {
        Epic epic = resolveEpic(Integer.parseInt(fields[5]), taskManager);
        return Subtask.createForDeserialization(Integer.parseInt(fields[0]), fields[2], fields[4], Status.valueOf(fields[3]), epic);
    }

    private static Epic createEpic(String[] fields) {
        return Epic.createForDeserialization(Integer.parseInt(fields[0]), fields[2], fields[4], Status.valueOf(fields[3]));
    }

}
