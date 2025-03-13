package ru.yandex.practicum.taskmanager.service;

import ru.yandex.practicum.taskmanager.model.Task;

import java.util.List;

public interface HistoryManager {

    void add(Task task);

    void remove(int id);

    List<Task> getHistory();

    void clearHistory();
}
