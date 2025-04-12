package ru.yandex.practicum.taskmanager.service.exception;

public class IllegalCsvFormatException extends TaskManagerException {
    public IllegalCsvFormatException(String message) {
        super(message);
    }

    public IllegalCsvFormatException(String message, Throwable cause) {
        super(message, cause);
    }

}
