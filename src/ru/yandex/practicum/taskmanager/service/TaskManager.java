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

//    a. Получение списка всех задач.
//    b. Удаление всех задач.
//    c. Получение по идентификатору.
//    d. Создание. Сам объект должен передаваться в качестве параметра.
//    e. Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
//    f. Удаление по идентификатору.
//Дополнительные методы:
//    a. Получение списка всех подзадач определённого эпика.

    private final Map<Integer, Task> tasks = new HashMap<>(); // tasks by id
    private final Map<Class<?>, List<Integer>> tasksByType = new HashMap<>(); // tasks by type

    private int idCounter = 1;

    // TODO перенести порядок следования метода по области видимости
    private int generateNextId() {
        return idCounter++;
    }

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
    // c отдельным ENUM некрасиво получается, так как все равно нужно соответствие классу задачи
    /*private TaskType getTaskType2(Task task) {
        return switch (task) {
            case Subtask ignored -> TaskType.SUBTASK;
            case Epic ignored -> TaskType.EPIC;
            default -> TaskType.TASK;
        };
    }*/

    // TODO пока не знаю как вымутить через Возвращение неизменяемого представления
    /*public Map<Integer, Task> getInnerTasks() {
        return Collections.unmodifiableMap(tasks);
    }
    */

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
                if (clazz.isInstance(task)) taskList.add(clazz.cast(new Task(task)));
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
        // TODO Обновляем статус эпиков или удаляем их все тоже?
        for (Epic epic : getEpics()) {
            epic.getSubtaskIds().clear();
            updateEpicStatus(epic);
        }
    }

    @Override
    public void deleteEpics() {
        /*for (int id : tasksByType.get(TaskType.EPIC)) {
            tasks.remove(id);
        }
        tasksByType.remove(TaskType.EPIC);*/
        deleteTasksByType(Subtask.class);
        deleteTasksByType(Epic.class);
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
                //return (T) task;
                return (T) new Task(task); // TODO через конструктор копирования
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
        /*if (isTaskExists(task)) {
            System.out.println(task.getClass().getSimpleName() + " with ID = " + task.getId() + " already exists.");
            return;
        }
        task.setId(generateNextId());
        tasks.put(task.getId(), task);
        tasksByType.computeIfAbsent(task.getClass(), k -> new ArrayList<>()).add(task.getId());
        */

        /*if (!task.hasId()) task.setId(generateId());
        tasks.put(task.getId(), task);
        tasksByType.get(task.getClass()).add(task.getId());*/
        createCommonTask(task, task.getClass());
    }

    @Override
    public void createSubtask(Subtask subtask, Epic epic) {
        /*if (isTaskExists(subtask)) {
            System.out.println(subtask.getClass().getSimpleName() + " with ID = " + subtask.getId() + " already exists.");
            return;
        }*/
        /*subtask.setId(generateId());
        tasks.put(subtask.getId(), subtask);
        tasksByType.get(getTaskType(subtask)).add(subtask.getId());*/
        createCommonTask(subtask, subtask.getClass());
        // TODO не должно вызываться если subtask не добавили
        if (epic != null) {
            epic.addSubtaskId(subtask.getId());
            updateEpicStatus(epic);
        }
    }

    @Override
    public void createEpic(Epic epic) {
        /*epic.setId(generateId());
        tasks.put(epic.getId(), epic);
        tasksByType.get(TaskType.EPIC).add(epic.getId());*/
        createCommonTask(epic, epic.getClass());
    }

    private boolean isTaskExists(Task task) {
        return ((task.hasId() && tasks.containsKey(task.getId())));
    }

    private <T extends Task> void createCommonTask(Task task, Class<T> clazz) {
        // TODO  d. Создание. Сам объект должен передаваться в качестве параметра.
        if (isTaskExists(task)) {
            throw new IllegalArgumentException(task.getClass().getSimpleName() + " with ID = " + task.getId() + " already exists.");
            //System.out.println(task.getClass().getSimpleName() + " with ID = " + task.getId() + " already exists.");
            // return;
        }
        //if ( (task.hasId() && tasks.containsKey(task.getId())) | (!task.hasId()) ) task.assignId(generateId());
        task.setId(generateNextId());
        var newTask = clazz.cast(new Task(task));
        tasks.put(newTask.getId(), newTask);
        tasksByType.computeIfAbsent(newTask.getClass(), k -> new ArrayList<>()).add(newTask.getId());


        /*Task taskWithId = new Task(task.getName(), task.getDescription()) {
            {
                setId(generateId()); // Устанавливаем ID через package-private метод
            }
        };*/

        //tasks.put(task.getId(), task);
        //tasksByType.computeIfAbsent(task.getClass(), k -> new ArrayList<>()).add(task.getId());

        //  tasksByType.get(task.getClass()).add(task.getId());
    }

    @Override
    public void updateTask(Task task) {
        Task newTask = new Task(task);
        // TODO тут можно положить некоррекный id
        tasks.put(newTask.getId(), newTask);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        // TODO проверка что такой сабтаск был, иначе это не обновление
        Subtask newSubtask = new Subtask(subtask);
        tasks.put(newSubtask.getId(), newSubtask);
        // TODO update epic status
        //updateEpicStatus(subtask.getEpicId());
    }

    @Override
    public void updateEpic(Epic epic) {
        // TODO проверка что такой эпик был, иначе это не обновление
        Epic newEpic = new Epic(epic);
        tasks.put(newEpic.getId(), newEpic);
        // TODO update epic status
        updateEpicStatus(newEpic);
    }

    @Override
    public void deleteTaskById(int id) {
        Task task = tasks.remove(id);
        if (task != null) {
            List<Integer> taskIds = tasksByType.get(task.getClass());
            if (taskIds != null) {
                taskIds.remove(Integer.valueOf(id)); // Удаляем идентификатор задачи
            }

            if (task instanceof Epic epic) {
                for (Integer subtaskId : epic.getSubtaskIds()) {
                    deleteTaskById(subtaskId); // Рекурсивно удаляем подзадачи
                }
            }

            if (task instanceof Subtask subtask) {
                Epic epic = getTaskById((subtask).getEpicId());
                if (epic != null) {
                    epic.removeSubtaskId(task.getId()); // Удаляем идентификатор подзадачи из Epic
                    updateEpic(epic);
                    updateEpicStatus(epic);
                }
            }
        }
    }

    // TODO чперенести в таксманаджер, т.к. в идеале epici наружу не отдаются, они все копии
    public void updateEpicStatus(Epic epic) {
        // Epic epic = getTaskById(epicId);
        //if (this == null) return;

        // TODO если у эпика нет подзадач или все они имеют статус NEW, то статус должен быть NEW.
        if (epic.getSubtaskIds().isEmpty()) {
            // TODO  придется апдейтить полностью объект эпика
            //epic.setStatus(TaskStatus.NEW);
            updateEpic(new Epic(epic,TaskStatus.NEW));
            return;
        }

        boolean allNew = true;
        boolean allDone = true;
        for (int subtaskId : epic.getSubtaskIds()) {
            // TODO добраться до сабтаска
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
            // TODO обновляем полностью Epic
            //epic.setStatus(TaskStatus.NEW);
            updateEpic(new Epic(epic,TaskStatus.NEW));
        } else if (allDone) {
            //this.setStatus(TaskStatus.DONE);
            updateEpic(new Epic(epic,TaskStatus.DONE));
        } else {
            //super.setStatus(TaskStatus.IN_PROGRESS);
            updateEpic(new Epic(epic,TaskStatus.IN_PROGRESS));
        }
    }

    // TODO    a. Получение списка всех подзадач определённого эпика.
    private List<Subtask> SubtasksByEpic(Epic epic) {
        List<Subtask> subtasks = new ArrayList<>();
        for (Integer subtaskId : epic.getSubtaskIds()) {
            Subtask subtask = getTaskById(subtaskId);
            // TODO тут не нужен повторный new, getTaskById уже отдал копи.
            if (subtask != null) subtasks.add(new Subtask(subtask));
        }
        return subtasks;
    }

}
