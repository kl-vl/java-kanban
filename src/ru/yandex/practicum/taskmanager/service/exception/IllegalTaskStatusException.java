package ru.yandex.practicum.taskmanager.service.exception;

public class IllegalTaskStatusException extends TaskManagerException {
    public IllegalTaskStatusException(String message) {
        super(message);
    }

    public IllegalTaskStatusException(String message, Throwable cause) {
        super(message, cause);
    }
}
