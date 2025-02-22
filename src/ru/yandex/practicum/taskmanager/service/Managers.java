package ru.yandex.practicum.taskmanager.service;

public class Managers {

    public static TaskManager getDefault() {
        return InMemoryTaskManager.getInstance();
    }

    public static HistoryManager getDefaultHistory() {
        return InMemoryHistoryManager.getInstance();
    }

}
