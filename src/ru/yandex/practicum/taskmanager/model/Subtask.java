package ru.yandex.practicum.taskmanager.model;

import java.util.Objects;

public class Subtask extends Task {
    private Epic epic;

    public Subtask(String name, String description) {
        super(0, name, description, Status.NEW);
    }

    private Subtask(int newId, Subtask other) {
        super(newId, other);
        this.epic = other.epic;
    }

    private Subtask(int id, String name, String description, Status status, Epic epic) {
        super(id, name, description, status);
        this.setEpic(Objects.requireNonNull(epic, "Epic must not be null"));
    }

    @Override
    public Subtask copy() {
        return new Subtask(super.getId(), this);
    }

    @Override
    public Subtask copy(int newId) {
        return new Subtask(newId, this);
    }

    @Override
    public Type getType() {
        return Type.SUBTASK;
    }

    public Epic getEpic() {
        return epic;
    }

    public void setEpic(Epic epic) {
        if (epic == null) {
            throw new IllegalArgumentException("Epic cannot be null.");
        }
        this.epic = epic;
    }

    public static Subtask createForDeserialization(int id, String name, String description, Status status, Epic epic) {
        return new Subtask(id, name, description, status, epic);
    }

    @Override
    String[] getFieldsForSerialization() {
        String[] fields = super.getFieldsForSerialization();
        fields[fields.length - 1] = getEpic() != null ? String.valueOf(getEpic().getId()) : "";
        return fields;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status='" + getStatus() + '\'' +
                ", epicId=" + ((getEpic() != null) ? getEpic().getId() : "") +
                '}';
    }
}
