package ru.yandex.practicum.taskmanager.service;

import ru.yandex.practicum.taskmanager.model.Epic;
import ru.yandex.practicum.taskmanager.model.Subtask;
import ru.yandex.practicum.taskmanager.model.Task;
import ru.yandex.practicum.taskmanager.model.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TaskManager implements TaskManagerInterface {

    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Class<?>, List<Integer>> tasksByType = new HashMap<>();

    private int idCounter = 1;

    private int generateNextId() {
        return idCounter++;
    }

    public TaskManager() {
        tasksByType.put(Task.class, new ArrayList<>());
        tasksByType.put(Subtask.class, new ArrayList<>());
        tasksByType.put(Epic.class, new ArrayList<>());
    }

    @Override
    public List<Task> getTasks() {
        return getTasksByType(Task.class);
    }

    @Override
    public List<Subtask> getSubtasks() {
        return getTasksByType(Subtask.class);
    }

    @Override
    public List<Epic> getEpics() {
        return getTasksByType(Epic.class);
    }

    private <T extends Task> List<T> getTasksByType(Class<T> clazz) {
        List<T> taskList = new ArrayList<>();
        if (!tasksByType.containsKey(clazz)) {
            return taskList;
        }
        for (int id : tasksByType.get(clazz)) {
            if (tasks.containsKey(id)) {
                var task = tasks.get(id);
                if (clazz.isInstance(task)) taskList.add(clazz.cast(task.copy()));
            }
        }
        return taskList;
    }

    @Override
    public void deleteTasks() {
        deleteTasksByType(Task.class);
    }

    @Override
    public void deleteSubtasks() {
        deleteTasksByType(Subtask.class);
        if (!tasksByType.containsKey(Epic.class)) {
            return;
        }
        for (int id : tasksByType.get(Epic.class)) {
            Optional<Epic> oEpic = getInternalTaskById(id);
            if (oEpic.isPresent()) {
                Epic epic = oEpic.get();
                epic.clearSubtaskIds();
                updateEpicStatusById(epic.getId());
            }
        }
    }

    @Override
    public void deleteEpics() {
        deleteTasksByType(Subtask.class);
        deleteTasksByType(Epic.class);
    }

    private <T> void deleteTasksByType(Class<T> clazz) {
        if (!tasksByType.containsKey(clazz)) {
            return;
        }
        for (int id : tasksByType.get(clazz)) {
            tasks.remove(id);
        }
        tasksByType.put(clazz, new ArrayList<>());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Task> Optional<T> getTaskById(int id) {
        if (tasks.containsKey(id)) {
            var task = tasks.get(id);
            if ((task != null)) {
                return Optional.of((T) task.copy());
            }
        }
        return Optional.empty();
    }

    @Override
    public void createTask(Task task) {
        if (isTaskExists(task)) {
            System.out.println("Error adding Task: Task with id = " + task.getId() + " already exists");
            return;
        }
        createCommonTask(task, task.getClass());
    }

    @Override
    public void createSubtask(Subtask subtask, Epic epic) {
        if (isTaskExists(subtask)) {
            System.out.println("Error adding Subtask: Subtask with id = " + subtask.getId() + " already exists");
            return;
        }
        if (!isTaskExists(epic)) {
            System.out.println("Error adding Subtask: Epic " + epic.getId() + " does not exists for Subtask " + subtask.getId());
            return;
        }
        subtask.setEpicId(epic.getId());
        int newSubTaskId = createCommonTask(subtask, subtask.getClass());

        Optional<Epic> oEpic = getInternalTaskById((epic.getId()));
        if (oEpic.isPresent()) {
            Epic internalEpic = oEpic.get();
            internalEpic.addSubtaskId(newSubTaskId);
        }
        updateEpicStatusById(epic.getId());
    }

    @Override
    public void createEpic(Epic epic) {
        if (isTaskExists(epic)) {
            System.out.println("Error adding Epic: Epic with id = " + epic.getId() + " already exists");
            return;
        }
        if (isTaskExists(epic)) return;
        createCommonTask(epic, epic.getClass());
    }

    private boolean isTaskExists(Task task) {
        return (((task.getId() != 0) && tasks.containsKey(task.getId())));
    }

    private <T extends Task> int createCommonTask(Task task, Class<T> clazz) {
        int newId = generateNextId();
        var newTask = clazz.cast(task.copy());
        newTask.setId(newId);
        tasks.put(newTask.getId(), newTask);
        tasksByType.computeIfAbsent(newTask.getClass(), k -> new ArrayList<>()).add(newTask.getId());
        return newId;
    }

    @Override
    public void updateTask(Task task) {
        if (!isTaskExists(task)) {
            System.out.println("Task " + task.getId() + " does not exists to update");
            return;
        }
        Task newTask = task.copy();
        tasks.put(newTask.getId(), newTask);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (!isTaskExists(subtask)) {
            System.out.println("Subtask " + subtask.getId() + " does not exists to update");
            return;
        }
        Subtask newSubtask = subtask.copy();
        tasks.put(newSubtask.getId(), newSubtask);
        updateEpicStatusById(newSubtask.getEpicId());
    }

    @Override
    public void updateEpic(Epic epic) {
        if (!isTaskExists(epic)) {
            System.out.println("Subtask " + epic.getId() + " does not exists to update");
            return;
        }
        Epic newEpic = epic.copy();
        tasks.put(newEpic.getId(), newEpic);
        updateEpicStatusById(newEpic.getId());
    }

    @Override
    public void deleteTaskById(int id) {
        Task task = tasks.remove(id);
        if (task != null) {
            List<Integer> taskIds = tasksByType.get(task.getClass());
            if (taskIds != null) {
                taskIds.remove(Integer.valueOf(id));
                task.onDelete(this);
            }
        }
    }

    public void updateEpicStatusById(int epicId) {
        Optional<Epic> oEpic = getInternalTaskById(epicId);
        if (oEpic.isEmpty()) return;
        Epic epic = oEpic.get();
        if (epic.getSubtaskIds().isEmpty()) {
            epic.getSubtaskIds().clear();
            epic.setStatus(TaskStatus.NEW);
            return;
        }

        boolean allNew = true;
        boolean allDone = true;
        for (int subtaskId : epic.getSubtaskIds()) {
            Optional<Subtask> oSubtask = getInternalTaskById(subtaskId);
            if (oSubtask.isEmpty()) continue;
            Subtask subtask = oSubtask.get();
            if (subtask.getStatus() != TaskStatus.NEW) {
                allNew = false;
            }
            if (subtask.getStatus() != TaskStatus.DONE) {
                allDone = false;
            }
        }

        if (allNew) {
            epic.setStatus(TaskStatus.NEW);
        } else if (allDone) {
            epic.setStatus(TaskStatus.DONE);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    @Override
    public List<Subtask> getSubtasksByEpic(Epic epic) {
        List<Subtask> subtasks = new ArrayList<>();
        Optional<Epic> oEpic = getInternalTaskById(epic.getId());
        if (oEpic.isEmpty()) return subtasks;
        for (Integer subtaskId : oEpic.get().getSubtaskIds()) {
            Optional<Subtask> optionalSubtask = getTaskById(subtaskId);
            if (optionalSubtask.isEmpty()) continue;
            Subtask subtask = optionalSubtask.get();
            subtasks.add(subtask);
        }
        return subtasks;
    }

    @SuppressWarnings("unchecked")
    private <T extends Task> Optional<T> getInternalTaskById(int id) {
        if (tasks.containsKey(id)) {
            var task = tasks.get(id);
            if ((task != null)) {
                return Optional.of((T) task);
            }
        }
        return Optional.empty();
    }


}
