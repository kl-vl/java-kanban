package ru.yandex.practicum.taskmanager.model;

import ru.yandex.practicum.taskmanager.service.FileBackedTaskManager;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private Epic epic;

    public Subtask(String name, String description) {
        super(0, name, description, Status.NEW, null, Duration.ZERO);
    }

    public Subtask(String name, String description, LocalDateTime startTime, Duration duration) {
        super(0, name, description, Status.NEW, startTime, duration);
    }

    private Subtask(int newId, Subtask other) {
        super(newId, other);
        this.epic = other.epic;
    }

    private Subtask(int id, String name, String description, Status status, LocalDateTime startTime, Duration duration, Epic epic) {
        super(id, name, description, status, startTime, duration);
        this.setEpic(epic);
    }

    @Override
    public Subtask copy() {
        return new Subtask(super.getId(), this);
    }

    @Override
    public Subtask copy(int newId) {
        return new Subtask(newId, this);
    }

    public Subtask copyWith(String name, String description, Status status, LocalDateTime startTime, Duration duration, Epic epic) {
        return new Subtask(
                super.getId(),
                name == null ? this.getName() : name,
                description == null ? this.getDescription() : description,
                status == null ? this.getStatus() : status,
                startTime == null ? this.getStartTime() : startTime,
                duration == null ? this.getDuration() : duration,
                epic == null ? this.epic : epic
        );
    }

    @Override
    public Type getType() {
        return Type.SUBTASK;
    }

    public Epic getEpic() {
        return epic;
    }

    public void setEpic(Epic epic) {
        this.epic = epic;
    }

    public static Subtask createForDeserialization(int id, String name, String description, Status status, Epic epic, LocalDateTime startTime, Duration duration) {
        return new Subtask(id, name, description, status, startTime, duration, epic);
    }

    @Override
    String[] getFieldsForSerialization() {
        String[] fields = super.getFieldsForSerialization();
        int epicFieldIndex = FileBackedTaskManager.getCsvFieldPosition("epic");
        if (epicFieldIndex != -1) {
            fields[epicFieldIndex] = getEpic() != null ? String.valueOf(getEpic().getId()) : "";
        }
        return fields;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                getBaseToString() +
                ", epicId=" + (epic != null ? epic.getId() : "") +
                '}';
    }

}
