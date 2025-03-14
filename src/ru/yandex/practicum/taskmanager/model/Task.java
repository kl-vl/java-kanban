package ru.yandex.practicum.taskmanager.model;

import java.util.Objects;

public class Task {
    private final int id;
    private String name;
    private String description;
    private Status status;

    public Task(String name, String description) {
        this(0, name, description, Status.NEW);
    }

    Task(int id, String name, String description, Status status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    Task(int newId, Task other) {
        this.id = newId;
        this.name = other.name;
        this.description = other.description;
        this.status = other.status;
    }

    public Task copy() {
        return new Task(this.id, this);
    }

    public Task copy(int newId) {
        return new Task(newId, this);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
