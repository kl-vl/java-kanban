package ru.yandex.practicum.taskmanager.service.exception;

public class ManagerTaskAlreadyExists extends Exception {
    public ManagerTaskAlreadyExists(String message) {
        super(message);
    }
}
