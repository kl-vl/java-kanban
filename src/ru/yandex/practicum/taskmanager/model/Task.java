package ru.yandex.practicum.taskmanager.model;

import java.util.Objects;

public class Task {
    private int id;
    private String name;
    private String description;
    private TaskStatus status;

    /*public Task() {
    }*/

    /*public Task(String name, String description, int id) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = TaskStatus.NEW;
    }*/

    public Task(final String name, final String description) {
        // this.id = 0; TODO 0 инициализируется по умолчанию, но можно явно указать, если бы был приватный конструктор с id
        this.name = name;
        this.description = description;
        // New task always has New status
        this.status = TaskStatus.NEW;
    }

    // Конструктор копирования
    public Task(final Task task) {
        this.id = task.id;
        this.name = task.name;
        this.description = task.description;
        this.status = task.status;
    }

    /*private Task(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = TaskStatus.NEW;
    }

    // TODO Фабричный метод для создания задачи с id не спасает от создания неуникального id
    public static Task createTask(int id, String name, String description) {
        return new Task(id, name, description);
    }

     */

    public int getId() {
        return id;
    }

    // TODO нарущает инкапуляцию если юзать извне
    // TODO Не можем установить метод assignId как package-private или protected
    public void setId(int id) {
        this.id = id;
    }

    // TODO убрать в таскманагер
    public boolean hasId() {
        return id != 0;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    // TODO нужен ли такой паблик доступ и как его спрятать
    public void setStatus(TaskStatus status) {
        if (!this.status.equals(status)) this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        //if (!(o instanceof Task task)) return false;
        // TODO instanceof не подходит
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(name, task.name) && Objects.equals(description, task.description) && status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, status);
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
