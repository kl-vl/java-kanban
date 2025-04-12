package ru.yandex.practicum.taskmanager.service.exception;

public class IllegalTaskDurationFormatException extends TaskManagerException {
    public IllegalTaskDurationFormatException(String message) {
        super(message);
    }

    public IllegalTaskDurationFormatException(String message, Throwable cause) {
        super(message, cause);
    }
}
