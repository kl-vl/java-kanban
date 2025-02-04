package ru.yandex.practicum.taskmanager.model;

import ru.yandex.practicum.taskmanager.service.TaskManager;
import ru.yandex.practicum.taskmanager.service.TaskManagerInterface;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    // List of Epic's Subtasks
    private final List<Integer> subtaskIds;
    //private TaskManagerInterface manager = new TaskManager();

    public Epic(final String name, final String description) {
        super(name, description);
        this.subtaskIds = new ArrayList<>();
    }

    public Epic(final Epic epic) {
        super(epic);
        this.subtaskIds = epic.subtaskIds;
        //this.manager = epic.manager;
    }

    public Epic(final Epic epic, final TaskStatus status) {
        super(epic);
        super.setStatus(status);
        this.subtaskIds = epic.subtaskIds;
    }

    public List<Integer> getSubtaskIds() {
        return new ArrayList<>(subtaskIds);
    }

    // TODO проверить необходимость метод и область видимости
    public void addSubtaskId(int subtaskId) {
        subtaskIds.add(subtaskId);
    }

    // TODO проверить необходимость метода и область видимости
    public void removeSubtaskId(int subtaskId) {
        subtaskIds.remove(Integer.valueOf(subtaskId));
    }


    // TODO эпику нельзя менять статус
    @Override
    public void setStatus(TaskStatus status) {
    }

    /*
    // TODO чперенести в таксманаджер, т.к. в идеале будут копии объектов наружу
    public void updateEpicStatus() {
        // Epic epic = getTaskById(epicId);
        //if (this == null) return;

        // TODO если у эпика нет подзадач или все они имеют статус NEW, то статус должен быть NEW.
        if (this.getSubtaskIds().isEmpty()) {
            this.setStatus(TaskStatus.NEW);
            return;
        }

        boolean allNew = true;
        boolean allDone = true;
        for (int subtaskId : this.getSubtaskIds()) {
            // TODO добраться до сабтаска
            Subtask subtask = manager.getTaskById(subtaskId);
            if (subtask == null) continue;
            if (subtask.getStatus() != TaskStatus.NEW) {
                allNew = false;
            }
            if (subtask.getStatus() != TaskStatus.DONE) {
                allDone = false;
            }
        }

        if (allNew) {
            // TODO обновляем через суперкласс
            super.setStatus(TaskStatus.NEW);
        } else if (allDone) {
            this.setStatus(TaskStatus.DONE);
        } else {
            super.setStatus(TaskStatus.IN_PROGRESS);
        }
    }*/

    @Override
    public String toString() {
        return "Epic{" +
                "subtaskIds=" + getSubtaskIds() +
                ", id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                '}';
    }
}
