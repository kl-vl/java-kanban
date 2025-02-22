package ru.yandex.practicum.taskmanager.service;

import ru.yandex.practicum.taskmanager.model.Task;

import java.util.List;

public interface HistoryManager {

    <T extends Task> void add(T task);

    List<Task> getHistory();
}
