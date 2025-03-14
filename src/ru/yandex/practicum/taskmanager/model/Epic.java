package ru.yandex.practicum.taskmanager.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Epic extends Task {
    private final List<Subtask> subtasksList;

    public Epic(String name, String description) {
        super(0, name, description,Status.NEW);
        this.subtasksList = new ArrayList<>();
    }

    private Epic(int newId, Epic other) {
        super(newId, other);
        this.subtasksList = new ArrayList<>(other.subtasksList);
    }

    @Override
    public Epic copy() {
        return new Epic(super.getId(), this);
    }

    public Epic copy(int newId) {
        return new Epic(newId, this);
    }

    public List<Subtask> getSubtasksList() {
        return Collections.unmodifiableList(subtasksList);
    }

    public void addSubtasksList(Subtask subtask) {
        if (subtask == null) {
            System.out.println("Subtask cannot be null.");
            return;
        }
        subtasksList.remove(subtask);
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
                ", status='" + getStatus() + '\'' +
                ", subtasksList=" + subtasksList.stream().map(subtask -> subtask.getId()).toList() +
                '}';
    }
}