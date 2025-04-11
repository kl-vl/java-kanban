package ru.yandex.practicum.taskmanager.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    private final List<Subtask> subtasksList;
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(0, name, description, Status.NEW, null, Duration.ZERO);
        this.subtasksList = new ArrayList<>();
        this.endTime = null;
    }

    private Epic(int id, String name, String description, Status status) {
        super(id, name, description, status, null, Duration.ZERO);
        this.subtasksList = new ArrayList<>();
        this.endTime = null;
    }

    private Epic(int newId, Epic other) {
        super(newId, other);
        this.subtasksList = new ArrayList<>(other.subtasksList);
        this.endTime = other.endTime;
    }

    @Override
    public Type getType() {
        return Type.EPIC;
    }

    @Override
    public Epic copy() {
        return new Epic(super.getId(), this);
    }

    @Override
    public Epic copy(int newId) {
        return new Epic(newId, this);
    }

    public Epic copyWith(String name, String description, Status status) {
        return new Epic(
                super.getId(),
                name == null ? this.getName() : name,
                description == null ? this.getDescription() : description,
                status == null ? this.getStatus() : status
        );
    }

    public List<Subtask> getSubtasksList() {
        return Collections.unmodifiableList(subtasksList);
    }

    public void addSubtasksList(Subtask subtask) {
        if (subtask == null) {
            return;
        }
        subtasksList.remove(subtask);
        subtasksList.add(subtask);
    }

    public void removeSubtask(Subtask subtask) {
        subtasksList.remove(subtask);
    }

    public static Epic createForDeserialization(int id, String name, String description, Status status) {
        return new Epic(id, name, description, status);
    }

    public LocalDateTime calcStartTime() {
        return subtasksList.stream().map(Subtask::getStartTime).filter(Objects::nonNull).min(LocalDateTime::compareTo).orElse(null);
    }

    public LocalDateTime calcEndTime() {
        return subtasksList.stream().map(Subtask::getEndTime).filter(Objects::nonNull).max(LocalDateTime::compareTo).orElse(null);
    }

    public Duration calcDuration() {
        return subtasksList.stream().map(Subtask::getDuration).filter(Objects::nonNull).reduce(Duration.ZERO, Duration::plus);
    }

    @Override
    public void setStartTime(LocalDateTime startTime) {
        super.setStartTime(startTime);
    }

    @Override
    public void setDuration(Duration duration) {
        super.setDuration(duration);
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "Epic{" +
                getBaseToString() +
                ", subtasksList=" + subtasksList.stream().map(Task::getId).toList() +
                '}';
    }
}