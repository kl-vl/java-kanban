package ru.yandex.practicum.taskmanager.service;

import ru.yandex.practicum.taskmanager.model.Epic;
import ru.yandex.practicum.taskmanager.model.Subtask;
import ru.yandex.practicum.taskmanager.model.Task;
import ru.yandex.practicum.taskmanager.model.TaskStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TaskManager implements TaskManagerInterface {

    /**
     * The simplest solution of three separate maps for code simplification, as Practicum bequeaths.
     * (Not extensible: needs new map and methods for CustomTask;
     * Code duplication: needs code duplication for task types;
     * Some lack of OOP,
     * Complexity of management operation on all tasks).
     */
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();

    private int idCounter = 1;

    private int generateNextId() {
        return idCounter++;
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public Optional<Task> getTaskById(int id) {
        Task task = tasks.get(id);
        return (task != null) ? Optional.of(task.copy()) : Optional.empty();
    }

    @Override
    public Optional<Subtask> getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        return (subtask != null) ? Optional.of(subtask.copy()) : Optional.empty();
    }

    @Override
    public Optional<Epic> getEpicById(int id) {
        Epic epic = epics.get(id);
        return (epic != null) ? Optional.of(epic.copy()) : Optional.empty();
    }

    @Override
    public void deleteTasks() {
        tasks.clear();
    }

    @Override
    public void deleteSubtasks() {
        subtasks.clear();
    }

    @Override
    public void deleteEpics() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public int addTask(Task task) {
        if (task == null) {
            System.out.println("Task cannot be null");
            return -1;
        }
        if (isTaskExists(task)) {
            System.out.printf("Task with id = %d already exists%n", task.getId());
            return -1;
        }
        Task internalTask = task.copy();
        internalTask.setId(generateNextId());
        tasks.put(internalTask.getId(), internalTask);
        return internalTask.getId();
    }

    @Override
    public int addSubtask(Subtask subtask, Epic epic) {
        if (subtask == null) {
            System.out.println("Subtask cannot be null");
            return -1;
        }
        if (epic == null) {
            System.out.println("Epic cannot be null");
            return -1;
        }
        if (isSubtaskExists(subtask)) {
            System.out.printf("Subtask with id = %d already exists%n", subtask.getId());
            return -1;
        }
        if (!isEpicExists(epic)) {
            System.out.printf("Epic with id = %d does not exists for Subtask with id =  %d%n", epic.getId(), subtask.getId());
            return -1;
        }
        Subtask internalSubtask = subtask.copy();
        internalSubtask.setId(generateNextId());
        internalSubtask.setEpic(epics.get(epic.getId()));
        subtasks.put(internalSubtask.getId(), internalSubtask);
        Epic internalEpic = epics.get(epic.getId());
        if (internalEpic != null) {
            internalEpic.addSubtask(internalSubtask);
            updateEpicStatusById(internalEpic.getId());
        }
        return internalSubtask.getId();
    }

    @Override
    public int addEpic(Epic epic) {
        if (epic == null) {
            System.out.println("Epic cannot be null");
            return -1;
        }
        if (isEpicExists(epic)) {
            System.out.printf("Epic with id = %d already exists%n", epic.getId());
            return -1;
        }
        Epic internalEpic = epic.copy();
        internalEpic.setId(generateNextId());
        epics.put(internalEpic.getId(), internalEpic);
        return internalEpic.getId();
    }

    private boolean isTaskExists(Task task) {
        return (((task.getId() != 0) && tasks.containsKey(task.getId())));
    }

    private boolean isSubtaskExists(Subtask subtask) {
        return (((subtask.getId() != 0) && subtasks.containsKey(subtask.getId())));
    }

    private boolean isEpicExists(Epic epic) {
        return (((epic.getId() != 0) && epics.containsKey(epic.getId())));
    }

    @Override
    public void updateTask(Task task) {
        if (task == null) {
            System.out.println("Task cannot be null");
            return;
        }
        if (!isTaskExists(task)) {
            System.out.printf("Task with id = %d does not exists to update%n", task.getId());
            return;
        }
        Task internalTask = task.copy();
        tasks.put(internalTask.getId(), internalTask);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtask == null) {
            System.out.println("Subtask cannot be null");
            return;
        }
        if (!isSubtaskExists(subtask)) {
            System.out.printf("Subtask with id = %d does not exists to update%n", subtask.getId());
            return;
        }
        Subtask newSubtask = subtask.copy();
        subtasks.put(newSubtask.getId(), newSubtask);
        Epic epic = newSubtask.getEpic();
        if (epic != null) {
            updateEpicStatusById(epic.getId());
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epic == null) {
            System.out.println("Epic cannot be null");
            return;
        }
        if (!isEpicExists(epic)) {
            System.out.printf("Epic with id = %d does not exists to update", epic.getId());
            return;
        }
        Epic internalEpic = epic.copy();
        tasks.put(internalEpic.getId(), internalEpic);
        updateEpicStatusById(internalEpic.getId());
    }

    @Override
    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    @Override
    public void deleteSubtaskById(int id) {
        Subtask subtask = subtasks.remove(id);
        Epic epic = subtask.getEpic();
        if (epic != null) {
            Epic internalEpic = epics.get(epic.getId());
            internalEpic.removeSubtask(subtask);
        }
    }

    @Override
    public void deleteEpicById(int id) {
        Epic epic = epics.remove(id);
        for (Subtask subtask : new ArrayList<>(epic.getSubtasksList())) {
            deleteTaskById(subtask.getId());
        }
    }

    public void updateEpicStatusById(int epicId) {
        Epic internalEpic = epics.get(epicId);
        if (internalEpic == null) {
            return;
        }
        if (internalEpic.getSubtasksList().isEmpty()) {
            internalEpic.setStatus(TaskStatus.NEW);
            return;
        }
        boolean allNew = true;
        boolean allDone = true;
        List<Subtask> epicSubtasks = internalEpic.getSubtasksList();
        for (Subtask subtask : epicSubtasks) {
            if (subtask.getStatus() != TaskStatus.NEW) {
                allNew = false;
            }
            if (subtask.getStatus() != TaskStatus.DONE) {
                allDone = false;
            }
        }

        if (allNew) {
            internalEpic.setStatus(TaskStatus.NEW);
        } else if (allDone) {
            internalEpic.setStatus(TaskStatus.DONE);
        } else {
            internalEpic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    @Override
    public List<Subtask> getSubtasksByEpic(Epic epic) {
        if (epic == null) {
            return Collections.emptyList();
        }
        return new ArrayList<>(epic.getSubtasksList());
    }


}
