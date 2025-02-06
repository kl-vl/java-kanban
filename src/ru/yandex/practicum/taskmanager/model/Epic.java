package ru.yandex.practicum.taskmanager.model;

import ru.yandex.practicum.taskmanager.service.TaskManagerInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    // Epic's Subtasks id collection
    private final List<Integer> subtaskIds;

    public Epic(String name, String description) {
        super(name, description);
        this.subtaskIds = new ArrayList<>();
    }

    public Epic(Epic epic) {
        super(epic);
        this.subtaskIds = epic.subtaskIds;
    }

    @Override
    public Epic copy() {
        return new Epic(this);
    }

    public List<Integer> getSubtaskIds() {
        return new ArrayList<>(subtaskIds);
    }

    public void addSubtaskId(int subtaskId) {
        subtaskIds.add(subtaskId);
    }

    public void removeSubtaskId(int subtaskId) {
        subtaskIds.remove(Integer.valueOf(subtaskId));
    }

    public void clearSubtaskIds() {
        subtaskIds.clear();
    }

    @Override
    public void onDelete(TaskManagerInterface manager) {
        for (Integer subtaskId : subtaskIds) {
            manager.deleteTaskById(subtaskId);
        }
        manager.updateEpic(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtaskIds, epic.subtaskIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtaskIds);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", subtaskIds=" + subtaskIds +
                '}';
    }

}
