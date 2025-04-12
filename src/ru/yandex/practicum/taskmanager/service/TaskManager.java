package ru.yandex.practicum.taskmanager.service;

import ru.yandex.practicum.taskmanager.model.Epic;
import ru.yandex.practicum.taskmanager.model.Subtask;
import ru.yandex.practicum.taskmanager.model.Task;
import ru.yandex.practicum.taskmanager.service.exception.InvalidManagerTaskException;

import java.util.List;
import java.util.Optional;

public interface TaskManager {

    // Lists of tasks, subtasks and epics
    List<Task> getTasks();

    List<Subtask> getSubtasks();

    List<Epic> getEpics();

    // Deletion of tasks, subtask and epics
    void deleteTasks();

    void deleteSubtasks();

    void deleteEpics();

    // Get tasks, subtask an epics by id
    Optional<Task> getTaskById(int id);

    Optional<Subtask> getSubtaskById(int id);

    Optional<Epic> getEpicById(int id);

    // Get epic by subtask
    Optional<Epic> getEpicBySubtask(Subtask subtask);

    // List subtasks by epic
    List<Subtask> getSubtasksByEpicId(int epicId);

    // Add task, subtask or epic to TaskManager
    int addTask(Task task) throws InvalidManagerTaskException;

    int addSubtask(Subtask subtask, Epic epic) throws InvalidManagerTaskException;

    int addEpic(Epic epic) throws InvalidManagerTaskException;

    // Update task, subtask or epic
    void updateTask(Task task) throws InvalidManagerTaskException;

    void updateSubtask(Subtask subtask) throws InvalidManagerTaskException;

    void updateEpic(Epic epic) throws InvalidManagerTaskException;

    // Delete task, subtask щr epic by id
    void deleteTaskById(int id);

    void deleteSubtaskById(int id);

    void deleteEpicById(int id);

    // Task history
    List<Task> getHistory();

    List<Task> getPrioritizedTasks();
}
