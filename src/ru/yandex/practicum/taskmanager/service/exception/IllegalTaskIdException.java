package ru.yandex.practicum.taskmanager.service.exception;

public class IllegalTaskIdException extends TaskManagerException {
    public IllegalTaskIdException(String message) {
        super(message);
    }

    public IllegalTaskIdException(String message, Throwable cause) {
        super(message, cause);
    }
}
