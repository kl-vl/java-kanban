package ru.yandex.practicum.taskmanager.service.exception;

public class IllegalTaskTypeException extends TaskManagerException {
    public IllegalTaskTypeException(String message) {
        super(message);
    }

    public IllegalTaskTypeException(String message, Throwable cause) {
        super(message, cause);
    }
}
