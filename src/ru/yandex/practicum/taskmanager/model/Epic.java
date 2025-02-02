package ru.yandex.practicum.taskmanager.model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task{
    // TODO Каждый эпик знает, какие подзадачи в него входят.
    private final List<Integer> subtaskIds;

    public Epic(String name, String description) {
        super(name, description);
        this.subtaskIds = new ArrayList<>();
    }

    public List<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void addSubtaskId(int subtaskId) {
        subtaskIds.add(subtaskId);
    }

    public void removeSubtaskId(int subtaskId) {
        subtaskIds.remove(Integer.valueOf(subtaskId));
    }

//    @Override
//    public void setStatus(TaskStatus status) {
//    }

    @Override
    public String toString() {
        // TODO json
        return "{ \"Epic\" : {" +
                "\"id\" : " + getId() +
                ", \"name\" : \"" + getName() +
                "\", \"description\" : \"" + getDescription() +
                "\", \"status\" : \"" + getStatus() +
                "\", \"subtaskIds\" : " + subtaskIds +
                "}}";
    }

}
