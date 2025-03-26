package ru.yandex.practicum.taskmanager.service.exception;

public class ManagerTaskNotFoundException extends Exception {
    public ManagerTaskNotFoundException(String message) {
        super(message);
    }
}
