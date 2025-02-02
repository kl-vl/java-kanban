package ru.yandex.practicum.taskmanager.model;

import java.util.Objects;

public class Task {
    private int id;
    private String name;
    private String description;
    private TaskStatus status;

    /*public Task(String name, String description, int id) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = TaskStatus.NEW;
    }*/

    public Task(String name, String description) {
        this.id = 0;
        this.name = name;
        this.description = description;
        this.status = TaskStatus.NEW;

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
//    protected void setId(int id) {
//        this.id = id;
//    }
//
    public void assignId(int id) { // Установим метод assignId как package-private или protected
        this.id = id;
    }

    public boolean hasId() { return id != 0; }

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
        if (!(o instanceof Task task)) return false;
        // TODO instanceof или getClass()?
        //if (o == null || getClass() != o.getClass()) return false;
        //Task task = (Task) o;
        return id == task.id && Objects.equals(name, task.name) && Objects.equals(description, task.description) && status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, status);
    }

    @Override
    public String toString() {
        return "{ \"Task\" : {" +
                "\"id\" : " + id +
                ", \"name\" : \"" + name +
                "\", \"description\" : \"" + description +
                "\", \"status\" : \"" + status +
                "\" }}";
    }

    // TODO
    /*
    public String getTaskInfo() {
        return "Task type: " + status + ", ID: " + status.getStatus();
    }
     */

}
