package ru.yandex.practicum.taskmanager.service.exception;

public class IllegalTaskDateTimeFormatException extends TaskManagerException {
    public IllegalTaskDateTimeFormatException(String message) {
        super(message);
    }

    public IllegalTaskDateTimeFormatException(String message, Throwable cause) {
        super(message, cause);
    }
}
