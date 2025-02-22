package ru.yandex.practicum.taskmanager.service;

import ru.yandex.practicum.taskmanager.model.Task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private static final int MAX_HISTORY_SIZE = 10;

    private final List<Task> tasksHistory = new ArrayList<>(MAX_HISTORY_SIZE);

    private static final InMemoryHistoryManager instance = new InMemoryHistoryManager();

    private InMemoryHistoryManager() {
    }

    public static InMemoryHistoryManager getInstance() {
        return instance;
    }

    public void clearHistory() {
        tasksHistory.clear();
    }

    @Override
    public <T extends Task> void add(T task) {
        if (task == null) return;
        limitTasksHistorySize();
        tasksHistory.add(task.copy());
    }

    @Override
    public List<Task> getHistory() {
        return Collections.unmodifiableList(tasksHistory);
    }

    private void limitTasksHistorySize() {
        //  TODO Если размер списка исчерпан, из него нужно удалить самый старый элемент — тот, который находится в начале списка.
        if (tasksHistory.size() >= MAX_HISTORY_SIZE) {
            tasksHistory.removeFirst();
        }
    }
}
