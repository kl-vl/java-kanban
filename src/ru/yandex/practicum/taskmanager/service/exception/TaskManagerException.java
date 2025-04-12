package ru.yandex.practicum.taskmanager.service.exception;

public class TaskManagerException extends RuntimeException {
    public TaskManagerException(String message) {
        super(message);
    }


    public TaskManagerException(String message, Throwable cause) {
        super(message, cause);
    }

}
