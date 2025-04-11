package ru.yandex.practicum.taskmanager.service;

import ru.yandex.practicum.taskmanager.model.Epic;
import ru.yandex.practicum.taskmanager.model.Status;
import ru.yandex.practicum.taskmanager.model.Subtask;
import ru.yandex.practicum.taskmanager.model.Task;
import ru.yandex.practicum.taskmanager.model.Type;
import ru.yandex.practicum.taskmanager.service.exception.InvalidManagerTaskException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class TaskDeserializer {

    private static final int CSV_FIELD_COUNT = 8;

    public static Task deserialize(String csvLine, TaskManager taskManager) throws InvalidManagerTaskException {
        String[] fields = parseCsvLine(csvLine);

        Type type = parseType(fields[1]);

        return switch (type) {
            case TASK -> createTask(fields);
            case SUBTASK -> createSubtask(fields, taskManager);
            case EPIC -> createEpic(fields);
        };
    }

    public static Task deserialize(String csvLine) throws InvalidManagerTaskException {
        return deserialize(csvLine, null);
    }

    private static String[] parseCsvLine(String csvLine) throws InvalidManagerTaskException {
        String[] fields = csvLine.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", CSV_FIELD_COUNT);
        if (fields.length != CSV_FIELD_COUNT) {
            throw new InvalidManagerTaskException("Invalid CSV format in line: " + csvLine);
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

    private static Type parseType(String typeField) throws InvalidManagerTaskException {
        try {
            return Type.valueOf(unescapeCsv(typeField));
        } catch (IllegalArgumentException e) {
            throw new InvalidManagerTaskException(String.format("Unknown task type '%s' in CSV line", typeField), e);
        }
    }

    private static Epic resolveEpic(Integer epicId, TaskManager manager) {
        if (epicId == null || manager == null)  {
            return null;
        }
        return manager.getEpicById(epicId).orElse(null);
    }

    private static Task createTask(String[] fields) throws InvalidManagerTaskException {
        return Task.createForDeserialization(
                parseId(fields[0]),         //id
                fields[2],                  //name
                fields[4],                  //description
                parseStatus(fields[3]),     //status
                parseDateTime(fields[6]),   //startTime
                parseDuration(fields[7])    //duration
        );
    }

    private static Subtask createSubtask(String[] fields, TaskManager taskManager) throws InvalidManagerTaskException {
        return Subtask.createForDeserialization(
                parseId(fields[0]),
                fields[2],
                fields[4],
                parseStatus(fields[3]),
                resolveEpic(parseId(fields[5]), taskManager), // epic
                parseDateTime(fields[6]),   //startTime
                parseDuration(fields[7])    //duration
        );
    }

    private static Epic createEpic(String[] fields) throws InvalidManagerTaskException {
        return Epic.createForDeserialization(
                // TODO у Эпика поля начала и продолжительности расчетные
                parseId(fields[0]),
                fields[2],
                fields[4],
                Status.valueOf(fields[3])
        );
    }

    private static Integer parseId(String idStr) throws InvalidManagerTaskException {
        try {
            if (idStr == null || idStr.isEmpty()) {
                return null;
            }
            return Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            throw new InvalidManagerTaskException("Invalid value of Task ID in CSV file: " + idStr, e);
        }
    }

    private static Status parseStatus(String statusStr) throws InvalidManagerTaskException {
        try {
            return Status.valueOf(statusStr);
        } catch (IllegalArgumentException e) {
            throw new InvalidManagerTaskException("Invalid value of Task STATUS in CSV file: " + statusStr, e);
        }
    }

    private static LocalDateTime parseDateTime(String dateTimeStr) throws InvalidManagerTaskException {
        try {
            if (dateTimeStr == null || dateTimeStr.isEmpty()) {
                return null;
            }
            return LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (DateTimeParseException e) {
            throw new InvalidManagerTaskException("Invalid value of Task STARTTIME in CSV file: " + dateTimeStr, e);
        }
    }

    private static Duration parseDuration(String durationStr) throws InvalidManagerTaskException {
        try {
            if (durationStr == null || durationStr.isEmpty()) return Duration.ZERO;
            long minutes = Long.parseLong(durationStr);
            if (minutes < 0) {
                throw new InvalidManagerTaskException("Invalid negative sign of value of Task DURATION in CSV file");
            }
            return Duration.ofMinutes(minutes);
        } catch (NumberFormatException e) {
            throw new InvalidManagerTaskException("Invalid value of Task DURATION in CSV file:: " + durationStr, e);
        }
    }

}
