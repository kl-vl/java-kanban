package ru.yandex.practicum.taskmanager.service;

import ru.yandex.practicum.taskmanager.model.Epic;
import ru.yandex.practicum.taskmanager.model.Subtask;
import ru.yandex.practicum.taskmanager.model.Task;
import ru.yandex.practicum.taskmanager.model.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskManager implements TaskManagerInterface {

    /*private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private int idCounter = 1;*/

    //    a. Получение списка всех задач.
//    b. Удаление всех задач.
//    c. Получение по идентификатору.
//    d. Создание. Сам объект должен передаваться в качестве параметра.
//    e. Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
//    f. Удаление по идентификатору.
//Дополнительные методы:
//    a. Получение списка всех подзадач определённого эпика.

    private final Map<Integer, Task> tasks = new HashMap<>(); // Все задачи
    private final Map<Class<?>, List<Integer>> tasksByType = new HashMap<>(); // Задачи по типу
    //private final Map<TaskType, List<Integer>> tasksByType2 = new HashMap<>(); // Задачи по типу

    private int idCounter = 1;

    public TaskManager() {
        // TODO коммент: Инициализация коллекций для каждого типа задач
        /*for (TaskType type : TaskType.values()) {
            tasksByType.put(type, new ArrayList<>());
        }*/
        tasksByType.put(Task.class, new ArrayList<>());
        tasksByType.put(Subtask.class, new ArrayList<>());
        tasksByType.put(Epic.class, new ArrayList<>());
    }

    // TODO Определение типа задачи
    /*private TaskType getTaskType2(Task task) {
        return switch (task) {
            case Subtask ignored -> TaskType.SUBTASK;
            case Epic ignored -> TaskType.EPIC;
            default -> TaskType.TASK;
        };
    }*/

    private int generateId() {
        return idCounter++;
    }

    @Override
    public List<Task> getTasks() {
        /*List<Task> tasks = new ArrayList<>();
        for (int id : tasksByType.get(TaskType.TASK)) {
            tasks.add((Subtask) tasks.get(id));
        }*/
        return getTasksByType(Task.class);
    }

    @Override
    public List<Subtask> getSubtasks() {
        /*List<Subtask> subtasks = new ArrayList<>();
        for (int id : tasksByType.get(TaskType.SUBTASK)) {
            subtasks.add((Subtask) tasks.get(id));
        }
        */
        return getTasksByType(Subtask.class);
    }

    @Override
    public List<Epic> getEpics() {
        /*List<Epic> epics = new ArrayList<>();
        for (int id : tasksByType.get(TaskType.EPIC)) {
            epics.add((Epic) tasks.get(id));
        }*/
        return getTasksByType(Epic.class);
    }


    private <T extends Task> List<T> getTasksByType(Class<T> clazz) {
        List<T> taskList = new ArrayList<>();
        for (int id : tasksByType.get(clazz)) {
            if (tasks.containsKey(id)) {
                Task task = tasks.get(id);
                if (clazz.isInstance(task)) taskList.add(clazz.cast(task));
            }
        }
        return taskList;
    }

    /*private <T extends Task> List<T> getTasksByType(TaskType type, Class<T> clazz) {
        List<T> taskList = new ArrayList<>();
        for (int id : tasksByType.get(type)) {
            if (tasks.containsKey(id)) {
                Task task = tasks.get(id);
                if (task != null) taskList.add((T) task);
            }
        }
        return taskList;
    }*/

    @Override
    public void deleteTasks() {
        /*for (int id : tasksByType.get(TaskType.TASK)) {
            tasks.remove(id);
        }
        tasksByType.remove(TaskType.TASK);*/
        deleteTasksByType(Task.class);
    }

    @Override
    public void deleteSubtasks() {
        /*for (int id : tasksByType.get(TaskType.SUBTASK)) {
            tasks.remove(id);
        }
        tasksByType.remove(TaskType.SUBTASK);*/
        deleteTasksByType(Subtask.class);
        // TODO Обновляем статус эпиков
        for (Epic epic : getEpics()) {
            epic.getSubtaskIds().clear();
            updateEpicStatusById(epic.getId());
        }
    }

    @Override
    public void deleteEpics() {
        /*for (int id : tasksByType.get(TaskType.EPIC)) {
            tasks.remove(id);
        }
        tasksByType.remove(TaskType.EPIC);*/
        deleteTasksByType(Epic.class);
        deleteSubtasks();
    }

    private <T> void deleteTasksByType(Class<T> clazz) {
        for (int id : tasksByType.get(clazz)) {
            tasks.remove(id);
        }
        tasksByType.remove(clazz);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Task> T getTaskById(int id) {
        if (tasks.containsKey(id)) {
            Task task = tasks.get(id);
            if ((task != null)) {
                // TODO Unchecked cast: 'ru.yandex.practicum.taskmanager.model.Task' to 'T'
                return (T) task;
            }
        }
        return null;
    }

    /*@Override
    public Subtask getSubtaskById(int id) {
        return (Subtask) tasks.get(id);
    }*/

    /*@Override
    public Epic getEpicById(int id) {
        return (Epic) tasks.get(id);
    }*/

    @Override
    public void createTask(Task task) {
        /*if (!task.hasId()) task.setId(generateId());
        tasks.put(task.getId(), task);
        tasksByType.get(task.getClass()).add(task.getId());*/
        createCommonTask(task);
    }

    @Override
    public void createSubtask(Subtask subtask) {
        /*subtask.setId(generateId());
        tasks.put(subtask.getId(), subtask);
        tasksByType.get(getTaskType(subtask)).add(subtask.getId());*/
        createCommonTask(subtask);

        Epic epic = getTaskById(subtask.getEpicId());
        if (epic != null) {
            epic.addSubtaskId(subtask.getId());
            updateEpicStatusById(epic.getId());
        }
    }

    @Override
    public void createEpic(Epic epic) {
        /*epic.setId(generateId());
        tasks.put(epic.getId(), epic);
        tasksByType.get(TaskType.EPIC).add(epic.getId());*/
        createCommonTask(epic);
    }

    private void createCommonTask(Task task) {
        //if (!task.hasId()) task.assignId(generateId());
        // TODO  d. Создание. Сам объект должен передаваться в качестве параметра.
        //if ( (task.hasId() && tasks.containsKey(task.getId())) | (!task.hasId()) ) task = Task.createTask(generateId(), task.getName(), task.getDescription());
        if ( (task.hasId() && tasks.containsKey(task.getId())) | (!task.hasId()) ) task.assignId(generateId());
        tasks.put(task.getId(), task);
        tasksByType.get(task.getClass()).add(task.getId());
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        tasks.put(subtask.getId(), subtask);
        // TODO update epic status
        updateEpicStatusById(subtask.getEpicId());
    }

    @Override
    public void updateEpic(Epic epic) {
        tasks.put(epic.getId(), epic);
        // TODO update epic status
        updateEpicStatusById(epic.getId());
    }

    @Override
    public void deleteTaskById(int id) {
        Task task = tasks.remove(id);
        if (task != null) {
            List<Integer> taskIds = tasksByType.get(task.getClass());
            if (taskIds != null) {
                taskIds.remove(Integer.valueOf(id)); // Удаляем идентификатор задачи
            }

            if (task instanceof Epic) {
                for (Integer subtaskId : ((Epic) task).getSubtaskIds()) {
                    deleteTaskById(subtaskId); // Рекурсивно удаляем подзадачи
                }
            }

            if (task instanceof Subtask) {
                Epic epic = getTaskById(((Subtask) task).getEpicId());
                if (epic != null) {
                    epic.removeSubtaskId(task.getId()); // Удаляем идентификатор подзадачи из Epic
                    updateEpicStatusById(epic.getId());
                }

            }

            /* TODO пересчет epica нужен?
            if (task.getClass() == Epic.class) {
                // TODO а если удалили epic нужно удалить подзадачи!
                //((Epic) task).removeSubtaskId(task.getId());
                for (int ids : ((Epic) task).getSubtaskIds()) {
                    deleteTaskById(ids);
                }
                updateEpicStatusById(task.getId());
            }
            if (task.getClass() == Subtask.class) {
                Epic epic = getTaskById(((Subtask) task).getEpicId());
                epic.removeSubtaskId(task.getId());
                updateEpicStatusById(((Subtask) task).getEpicId());
            }*/

        }
    }

    @Override
    public List<Subtask> getSubtasksByEpic(Epic epic) {
        List<Subtask> subtasks = new ArrayList<>();
        if (epic != null) {
            for (Integer subtaskId : epic.getSubtaskIds()) {
                Subtask subtask = getTaskById(subtaskId);
                if (subtask != null) subtasks.add(subtask);
            }
        }
        return subtasks;
    }

    private void updateEpicStatusById(int epicId) {
        Epic epic = getTaskById(epicId);
        if (epic == null) return;

        // TODO если у эпика нет подзадач или все они имеют статус NEW, то статус должен быть NEW.
        if (epic.getSubtaskIds().isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }

        boolean allNew = true;
        boolean allDone = true;
        for (int subtaskId : epic.getSubtaskIds()) {
            Subtask subtask = getTaskById(subtaskId);
            if (subtask == null) continue;
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
}
