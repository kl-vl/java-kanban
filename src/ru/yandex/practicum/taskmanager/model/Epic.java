package ru.yandex.practicum.taskmanager.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Epic extends Task {
    private final List<Subtask> subtasksList;

    public Epic(String name, String description) {
        super(name, description);
        this.subtasksList = new ArrayList<>();
    }

    public Epic(Epic other) {
        super(other);
        this.subtasksList = new ArrayList<>(other.subtasksList);
    }

    @Override
    public Epic copy() {
        return new Epic(this);
    }

    public List<Subtask> getSubtasksList() {
        return Collections.unmodifiableList(subtasksList);
    }

    public void addSubtask(Subtask subtask) {
        subtasksList.add(subtask);
    }

    public void removeSubtask(Subtask subtask) {
        subtasksList.remove(subtask);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", subtasksList=" + subtasksList.stream().map(subtask -> subtask.getId()).toList() +
                '}';
    }
}