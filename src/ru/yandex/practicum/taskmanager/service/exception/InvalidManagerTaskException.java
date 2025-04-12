package ru.yandex.practicum.taskmanager.service.exception;

public class InvalidManagerTaskException extends Exception {
    public InvalidManagerTaskException(String message) {
        super(message);
    }

    public InvalidManagerTaskException(String message, Throwable cause) {
        super(message, cause);
    }
}
