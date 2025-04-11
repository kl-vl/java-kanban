package ru.yandex.practicum.taskmanager.service;

import ru.yandex.practicum.taskmanager.model.Epic;
import ru.yandex.practicum.taskmanager.model.Status;
import ru.yandex.practicum.taskmanager.model.Subtask;
import ru.yandex.practicum.taskmanager.model.Task;
import ru.yandex.practicum.taskmanager.model.Type;
import ru.yandex.practicum.taskmanager.service.exception.InvalidManagerTaskException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Optional;
import java.util.TreeSet;

public class InMemoryTaskManager implements TaskManager {

    /**
     * In-memory storage of tasks in separate maps by task type
     */
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();

    // TODO
    //private final Map<Integer, Task> tasksById = new HashMap<>();
    //private final EnumMap<Type, Set<Task>> tasksByType = new EnumMap<>(Type.class);


/*    private final TreeSet<Task> sortedTasks2 = new TreeSet<>(
            Comparator.nullsLast(
                    Comparator.comparing(Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder()))
                            .thenComparingInt(Task::getId) // или другой уникальный идентификатор
            );*/

    //    private final NavigableSet<Task> tasksByTime = new TreeSet<>(
//            (task1, task2) -> {
//                // TODO подумать еще что возвращать для пустышек или как с ними поступать если они сюда попали
//                // Дата начала задачи по каким-то причинам может быть не задана. Тогда при добавлении её не следует учитывать в списке задач и подзадач, отсортированных по времени начала. Такая задача не влияет на приоритет других, а при попадании в список может сломать логику работы компаратора.
//
//                // Обе задачи без времени
//                if (task1.getStartTime() == null && task2.getStartTime() == null) {
//                    return Integer.compare(task1.getId(), task2.getId()); // сортируем по id
//                }
//
//                // Одна из задач без времени (null идёт в конец)
//                if (task1.getStartTime() == null) {
//                    return 1; // task1 > task2
//                }
//                if (task2.getStartTime() == null) {
//                    return -1; // task1 < task2
//                }
//
//                // Обе задачи со временем
//                int timeComparison = task1.getStartTime().compareTo(task2.getStartTime());
//                if (timeComparison != 0) {
//                    return timeComparison;
//                }
//
//                // Если startTime совпадает, сравниваем по id (чтобы избежать дубликатов)
//                //return Integer.compare(task1.getId(), task2.getId());
//                return task1.equals(task2) ? 0 : 1;
//            });
    private int idCounter;
    private final HistoryManager historyManager;

    //Comparator<Task> taskComparator = Comparator.comparingInt(Task::getId).thenComparing(Task::getStartTime);
    //Comparator<Task> taskComparator = Comparator.comparing(Task::getStartTime);
    //private final NavigableSet<Task> tasksByTime = new TreeSet<>(Comparator.comparing(Task::getStartTime));
    /**
     *
     */
    private final Comparator<Task> taskTimeComparator = Comparator
            .comparing(Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder()));
    private final NavigableSet<Task> tasksByTime = new TreeSet<>(taskTimeComparator);


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
        for (Task task : tasks.values()) {  // O(m)
            if (task.getStartTime() != null) {
                tasksByTime.remove(task);  // O(log n)
            }
            removeFromHistory(task.getId());
        }
        tasks.clear();
//TODO
//        tasks.keySet().forEach(this::removeFromHistory);
//        tasks.clear();
//        tasksByTime.removeIf(task -> task.getType() == Type.TASK);
    }

    @Override
    public void deleteSubtasks() {
        Iterator<Subtask> iterator = subtasks.values().iterator();
        while (iterator.hasNext()) {
            Subtask subtask = iterator.next();
            removeFromHistory(subtask.getId());
            if (subtask.getStartTime() != null) {
                tasksByTime.remove(subtask);
            }
            Epic epic = subtask.getEpic();
            iterator.remove();
            epic.removeSubtask(subtask);
            updateEpicById(epic.getId());
        }
        // TODO
        //tasksByTime.removeIf(task -> task.getType() == Type.SUBTASK);

    }

    @Override
    public void deleteEpics() {
//        for (int epicId : epics.keySet()) {
//            removeFromHistory(epicId);
//        }
        epics.keySet().forEach(this::removeFromHistory);
        epics.clear();
//        for (int subtaskId : subtasks.keySet()) {
//            removeFromHistory(subtaskId);
//        }
        subtasks.keySet().forEach(this::removeFromHistory);
        subtasks.clear();
        // TODO epic не сохраняется в приоритетных задачах
        //tasksByTime.removeIf(task -> task.getType() == Type.EPIC);

    }

    public void clearAll() {
        tasks.clear();
        subtasks.clear();
        epics.clear();
        historyManager.clearHistory();
        tasksByTime.clear();
    }

    @Override
    public int addTask(Task task) throws InvalidManagerTaskException {
        if (task == null) {
            throw new InvalidManagerTaskException("Task cannot be null.");
        }
        Task internalTask = task.copy(generateNextId());
        addToTaskByTime(internalTask);
        internalTask.setStatus(Status.NEW);
        tasks.put(internalTask.getId(), internalTask);
        return internalTask.getId();
    }

    @Override
    public int addSubtask(Subtask subtask, Epic epic) throws InvalidManagerTaskException {
        if (subtask == null) {
            throw new InvalidManagerTaskException("Subtask cannot be null.");
        }
        if (epic == null) {
            throw new InvalidManagerTaskException("Epic cannot be null.");
        }
        if (!isEpicExists(epic)) {
            throw new InvalidManagerTaskException(String.format("Epic with ID=%d does not exists in manager for Subtask addition", epic.getId()));
        }
        Subtask internalSubtask = subtask.copy(generateNextId());
        addToTaskByTime(internalSubtask);
        internalSubtask.setStatus(Status.NEW);
        // TODO проверка на существование эпика внутри!
        internalSubtask.setEpic(epics.get(epic.getId()).copy());
        subtasks.put(internalSubtask.getId(), internalSubtask);
        Epic internalEpic = epics.get(epic.getId());
        if (internalEpic != null) {
            internalEpic.addSubtasksList(internalSubtask.copy());
            updateEpicById(internalEpic.getId());
        }
        return internalSubtask.getId();
    }

    @Override
    public int addEpic(Epic epic) throws InvalidManagerTaskException {
        if (epic == null) {
            throw new InvalidManagerTaskException("Epic cannot be null.");
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
    public void updateTask(Task task) throws InvalidManagerTaskException {
        if (task == null) {
            throw new InvalidManagerTaskException("Task cannot be null.");
        }
        if (!isTaskExists(task)) {
            throw new InvalidManagerTaskException(String.format("Task ID=%d does not exists to update.", task.getId()));
        }
        Task internalTask = task.copy();
        addToTaskByTime(internalTask);
        tasks.put(internalTask.getId(), internalTask);

    }

    @Override
    public void updateSubtask(Subtask subtask) throws InvalidManagerTaskException {
        if (subtask == null) {
            throw new InvalidManagerTaskException("Subtask cannot be null.");
        }
        if (!isSubtaskExists(subtask)) {
            throw new InvalidManagerTaskException(String.format("Subtask ID=%d does not exists to update.", subtask.getId()));
        }
        Subtask internalSubtask = subtask.copy();
        addToTaskByTime(internalSubtask);
        subtasks.put(internalSubtask.getId(), internalSubtask);
        Epic epic = epics.get(subtask.getEpic().getId());
        if (epic != null) {
            epic.addSubtasksList(internalSubtask);
            updateEpicById(epic.getId());
        }
    }

    @Override
    public void updateEpic(Epic epic) throws InvalidManagerTaskException {
        if (epic == null) {
            throw new InvalidManagerTaskException("Epic cannot be null.");
        }
        if (!isEpicExists(epic)) {
            throw new InvalidManagerTaskException(String.format("Epic ID=%d does not exists to update.", epic.getId()));
        }
        Epic internalEpic = epic.copy();
        tasks.put(internalEpic.getId(), internalEpic);
        updateEpicById(internalEpic.getId());
        // TODO эпик контролировать не надо по времени
        //addToTaskByTime(internalEpic);
    }

    @Override
    public void deleteTaskById(int id) {
        Task oldTask = tasks.remove(id);
        if (oldTask != null) {
            removeFromHistory(id);
            removeFromTaskByTime(oldTask);
        }
    }

    @Override
    public void deleteSubtaskById(int id) {
        Subtask oldSubtask = subtasks.remove(id);
        if (oldSubtask == null) {
            return;
        }
        removeFromHistory(id);
        removeFromTaskByTime(oldSubtask);
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
        removeFromTaskByTime(oldEpic);
//        for (Subtask subtask : new ArrayList<>(oldEpic.getSubtasksList())) {
//            deleteSubtaskById(subtask.getId());
//        }
        oldEpic.getSubtasksList().stream()
                .map(Subtask::getId)
                .forEach(this::deleteSubtaskById);
    }

    public void updateEpicStatus(Epic internalEpic) {
        /*Epic internalEpic = epics.get(epicId);*/
        if (internalEpic == null) {
            return;
        }
        List<Subtask> subtasks = internalEpic.getSubtasksList();

        if (subtasks.isEmpty()) {
            internalEpic.setStatus(Status.NEW);
            return;
        }

        boolean allNew = subtasks.stream()
                .allMatch(subtask -> subtask.getStatus() == Status.NEW);

        boolean allDone = !allNew && subtasks.stream()
                .allMatch(subtask -> subtask.getStatus() == Status.DONE);

        Status newStatus = allNew ? Status.NEW : allDone ? Status.DONE : Status.IN_PROGRESS;
        if (internalEpic.getStatus() != newStatus) {
            internalEpic.setStatus(newStatus);
        }

        /*boolean allNew = true;
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
        }*/
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        // TODO
        return tasksByTime.stream().toList();
    }

    private void addToTaskByTime(Task task) throws InvalidManagerTaskException {
        if (task == null || task.getStartTime() == null) {
            return;
        }
        Task internalTask = switch (task.getType()) {
            case TASK -> tasks.get(task.getId());
            case SUBTASK -> subtasks.get(task.getId());
            case EPIC -> epics.get(task.getId());
        };
        boolean isRemoved = false;
        if (internalTask != null) {
            isRemoved = tasksByTime.remove(internalTask);
        }
        try {
            if (hasTimeIntersectionWithAnyTask(task)) {
                throw new InvalidManagerTaskException(task.getType() + " with startTime=" + task.getStartTime() + " has intersection with managers tasks");
            }
        } finally {
            // TODO finally возвращает задачу обратно
            //if (isRemoved && hasTimeIntersectionWithAnyTask(updatedTask)) {
            if (isRemoved) {
                tasksByTime.add(internalTask);
            }
        }
        tasksByTime.add(task);
    }

    private void removeFromTaskByTime(Task task) {
        if (task == null) {
            return;
        }
        tasksByTime.remove(task);
    }

    private void updateEpicDuration(Epic internalEpic) {
        if (internalEpic == null) {
            return;
        }
        internalEpic.setStartTime(internalEpic.calcStartTime());
        internalEpic.setEndTime(internalEpic.calcEndTime());
        internalEpic.setDuration(internalEpic.calcDuration());
    }

    private void updateEpicById(int epicId) {
        Epic internalEpic = epics.get(epicId);
        if (internalEpic == null) {
            return;
        }
        updateEpicStatus(internalEpic);
        updateEpicDuration(internalEpic);
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

    /** TODO
     * Реализуйте метод, который будет проверять, пересекаются ли две задачи по времени выполнения,
     * и возвращать true или false. Для его реализации используйте математический метод наложения отрезков.
     */
    private boolean hasTimeIntersectionTasks(Task task1, Task task2) {
        if (task1 == null || task2 == null) {
            return false;
        }
        if (task1.getStartTime() == null || task2.getStartTime() == null) {
            return false;
        }
        //return task1.getStartTime().isBefore(task2.getEndTime()) && task2.getStartTime().isBefore(task1.getEndTime());
        return !task1.getEndTime().isBefore(task2.getStartTime()) &&
                !task2.getEndTime().isBefore(task1.getStartTime());
    }

    /** TODO
     * Добавьте метод проверяющий пересекается ли задача с любой другой в списке менеджера
     */
    private boolean hasTimeIntersectionWithAnyTask(Task newTask) {
        if (newTask.getStartTime() == null) {
            return false;
        }
        Task lowerTask = tasksByTime.lower(newTask);
        if (hasTimeIntersectionTasks(lowerTask, newTask)) {
            return true;
        }
        Task higherTask = tasksByTime.higher(newTask);
        if (hasTimeIntersectionTasks(higherTask, newTask)) {
            return true;
        }
        return false;
    }

    static class TaskManagerHelper {
        private final InMemoryTaskManager manager;

        private TaskManagerHelper(InMemoryTaskManager manager) {
            this.manager = manager;
        }

        void addInternal(Task task) throws InvalidManagerTaskException {
            if (task == null) {
                throw new InvalidManagerTaskException("Task cannot be null.");
            }
            Type type = task.getType();
            if (type == Type.TASK) {
                if (manager.isTaskExists(task)) {
                    throw new InvalidManagerTaskException(String.format("Task ID=%d (%s) already exists.", task.getId(), task.getName()));
                }
                manager.tasks.put(task.getId(), task);
            } else if (type == Type.SUBTASK) {
                Subtask subtask = (Subtask) task;
                if (manager.isSubtaskExists(subtask)) {
                    throw new InvalidManagerTaskException(String.format("Subtask ID=%d (%s) already exists.", subtask.getId(), subtask.getName()));
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
                    throw new InvalidManagerTaskException(String.format("Epic ID=%d (%s) already exists.", epic.getId(), epic.getName()));
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
