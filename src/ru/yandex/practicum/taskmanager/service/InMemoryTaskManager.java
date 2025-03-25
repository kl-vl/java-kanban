package ru.yandex.practicum.taskmanager.service;

import ru.yandex.practicum.taskmanager.model.Epic;
import ru.yandex.practicum.taskmanager.model.Status;
import ru.yandex.practicum.taskmanager.model.Subtask;
import ru.yandex.practicum.taskmanager.model.Task;
import ru.yandex.practicum.taskmanager.model.Type;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryTaskManager implements TaskManager {

    /**
     * In-memory storage of tasks in separate maps by task type
     */
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private int idCounter;
    private final HistoryManager historyManager;

    InMemoryTaskManager() {
        idCounter = 1;
        historyManager = Managers.getDefaultHistory();
    }

    private int generateNextId() {
        return idCounter++;
    }

    private void addToHistory(Task task) {
        historyManager.add(task);
    }

    private void removeFromHistory(int id) {
        historyManager.remove(id);
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
        if (task == null) {
            return Optional.empty();
        }
        addToHistory(task);
        return Optional.of(task.copy());
    }

    @Override
    public Optional<Subtask> getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask == null) {
            return Optional.empty();
        }
        addToHistory(subtask);
        return Optional.of(subtask.copy());
    }

    @Override
    public Optional<Epic> getEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic == null) {
            return Optional.empty();
        }
        addToHistory(epic);
        return Optional.of(epic.copy());
    }

    @Override
    public void deleteTasks() {
        for (int taskId : tasks.keySet()) {
            removeFromHistory(taskId);
        }
        tasks.clear();
    }

    @Override
    public void deleteSubtasks() {
        Iterator<Subtask> iterator = subtasks.values().iterator();
        while (iterator.hasNext()) {
            Subtask subtask = iterator.next();
            removeFromHistory(subtask.getId());
            Epic epic = subtask.getEpic();
            iterator.remove();
            epic.removeSubtask(subtask);
            updateEpicStatusById(epic.getId());
        }
    }

    @Override
    public void deleteEpics() {
        for (int epicId : epics.keySet()) {
            removeFromHistory(epicId);
        }
        epics.clear();
        for (int subtaskId : subtasks.keySet()) {
            removeFromHistory(subtaskId);
        }
        subtasks.clear();
    }

    public void clearAll() {
        tasks.clear();
        subtasks.clear();
        epics.clear();
        historyManager.clearHistory();
    }

    @Override
    public int addTask(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task name cannot be null.");
        }
        Task internalTask = task.copy(generateNextId());
        internalTask.setStatus(Status.NEW);
        tasks.put(internalTask.getId(), internalTask);
        return internalTask.getId();
    }

    @Override
    public int addSubtask(Subtask subtask, Epic epic) {
        if (subtask == null) {
            throw new IllegalArgumentException("Subtask cannot be null.");
        }
        if (epic == null) {
            throw new IllegalArgumentException("Epic cannot be null.");
        }
        if (!isEpicExists(epic)) {
            throw new IllegalArgumentException(String.format("Epic with ID=%d does not exists in manager for Subtask addition", epic.getId()));
        }
        Subtask internalSubtask = subtask.copy(generateNextId());
        internalSubtask.setStatus(Status.NEW);
        internalSubtask.setEpic(epics.get(epic.getId()).copy());
        subtasks.put(internalSubtask.getId(), internalSubtask);
        Epic internalEpic = epics.get(epic.getId());
        if (internalEpic != null) {
            internalEpic.addSubtasksList(internalSubtask.copy());
            updateEpicStatusById(internalEpic.getId());
        }
        return internalSubtask.getId();
    }

    @Override
    public int addEpic(Epic epic) {
        if (epic == null) {
            throw new IllegalArgumentException("Epic cannot be null.");
        }
        Epic internalEpic = epic.copy(generateNextId());
        internalEpic.setStatus(Status.NEW);
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
            throw new IllegalArgumentException("Task cannot be null.");
        }
        if (!isTaskExists(task)) {
            throw new IllegalArgumentException(String.format("Task ID=%d does not exists to update.", task.getId()));
        }
        Task internalTask = task.copy();
        tasks.put(internalTask.getId(), internalTask);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtask == null) {
            throw new IllegalArgumentException("Subtask cannot be null.");
        }
        if (!isSubtaskExists(subtask)) {
            throw new IllegalArgumentException(String.format("Subtask ID=%d does not exists to update.", subtask.getId()));
        }
        Subtask newSubtask = subtask.copy();
        subtasks.put(newSubtask.getId(), newSubtask);
        Epic epic = epics.get(subtask.getEpic().getId());
        if (epic != null) {
            epic.addSubtasksList(newSubtask);
            updateEpicStatusById(epic.getId());
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epic == null) {
            throw new IllegalArgumentException("Epic cannot be null.");
        }
        if (!isEpicExists(epic)) {
            throw new IllegalArgumentException(String.format("Epic ID=%d does not exists to update.", epic.getId()));
        }
        Epic internalEpic = epic.copy();
        tasks.put(internalEpic.getId(), internalEpic);
        updateEpicStatusById(internalEpic.getId());
    }

    @Override
    public void deleteTaskById(int id) {
        Task oldTask = tasks.remove(id);
        if (oldTask != null) {
            removeFromHistory(id);
        }
    }

    @Override
    public void deleteSubtaskById(int id) {
        Subtask oldSubtask = subtasks.remove(id);
        if (oldSubtask == null) {
            return;
        }
        removeFromHistory(id);
        Epic epic = oldSubtask.getEpic();
        if (epic != null) {
            Epic internalEpic = epics.get(epic.getId());
            if (internalEpic != null) {
                internalEpic.removeSubtask(oldSubtask);
            }
        }
    }

    @Override
    public void deleteEpicById(int id) {
        Epic oldEpic = epics.remove(id);
        if (oldEpic == null) {
            return;
        }
        removeFromHistory(id);
        for (Subtask subtask : new ArrayList<>(oldEpic.getSubtasksList())) {
            deleteSubtaskById(subtask.getId());
        }
    }

    public void updateEpicStatusById(int epicId) {
        Epic internalEpic = epics.get(epicId);
        if (internalEpic == null) {
            return;
        }
        if (internalEpic.getSubtasksList().isEmpty()) {
            internalEpic.setStatus(Status.NEW);
            return;
        }
        boolean allNew = true;
        boolean allDone = true;
        List<Subtask> epicSubtasks = internalEpic.getSubtasksList();
        for (Subtask subtask : epicSubtasks) {
            if (subtask.getStatus() != Status.NEW) {
                allNew = false;
            }
            if (subtask.getStatus() != Status.DONE) {
                allDone = false;
            }
            if (!allNew && !allDone) {
                break;
            }
        }

        if (allNew) {
            internalEpic.setStatus(Status.NEW);
        } else if (allDone) {
            internalEpic.setStatus(Status.DONE);
        } else {
            internalEpic.setStatus(Status.IN_PROGRESS);
        }
    }

    @Override
    public List<Subtask> getSubtasksByEpicId(int epicId) {
        Epic internalEpic = epics.get(epicId);
        if (internalEpic == null) {
            return Collections.emptyList();
        }
        return new ArrayList<>(internalEpic.getSubtasksList());
    }

    @Override
    public Optional<Epic> getEpicBySubtask(Subtask subtask) {
        if (subtask == null) {
            return Optional.empty();
        }
        Subtask subtaskInternal = subtasks.get(subtask.getId());
        if (subtaskInternal == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(subtaskInternal.getEpic());
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    static class TaskManagerHelper {
        private final InMemoryTaskManager manager;

        private TaskManagerHelper(InMemoryTaskManager manager) {
            this.manager = manager;
        }

        void addInternal(Task task) {
            if (task == null) {
                throw new IllegalArgumentException("Added Task cannot be null.");
            }
            Type type = task.getType();
            if (type == Type.TASK) {
                if (manager.isTaskExists(task)) {
                    throw new IllegalArgumentException(String.format("Task ID=%d (%s) already exists.", task.getId(), task.getName()));
                }
                manager.tasks.put(task.getId(), task);
            } else if (type == Type.SUBTASK) {
                Subtask subtask = (Subtask) task;
                if (manager.isSubtaskExists(subtask)) {
                    throw new IllegalArgumentException(String.format("Subtask ID=%d (%s) already exists.", subtask.getId(), subtask.getName()));
                }
                manager.subtasks.put(subtask.getId(), subtask);
                Epic epic = subtask.getEpic();
                if (epic != null) {
                    Epic internalEpic = manager.epics.get(epic.getId());
                    if (internalEpic != null) {
                        internalEpic.addSubtasksList(subtask);
                    }
                }
            } else if (type == Type.EPIC) {
                Epic epic = (Epic) task;
                if (manager.isEpicExists(epic)) {
                    throw new IllegalArgumentException(String.format("Epic ID=%d (%s) already exists.", epic.getId(), epic.getName()));
                }
                manager.epics.put(task.getId(), (Epic) task);
            }
        }

        void setIdCounter(int idCounter) {
            manager.idCounter = idCounter;
        }

    }

    TaskManagerHelper getHelper() {
        return new TaskManagerHelper(this);
    }

}
