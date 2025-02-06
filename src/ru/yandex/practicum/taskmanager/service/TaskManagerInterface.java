package ru.yandex.practicum.taskmanager.service;

import ru.yandex.practicum.taskmanager.model.Epic;
import ru.yandex.practicum.taskmanager.model.Subtask;
import ru.yandex.practicum.taskmanager.model.Task;

import java.util.List;
import java.util.Optional;

public interface TaskManagerInterface {

    // List of tasks
    List<Task> getTasks();
    List<Subtask> getSubtasks();
    List<Epic> getEpics();

    // Delete tasks
    void deleteTasks();
    void deleteSubtasks();
    void deleteEpics();

    // Get Task by id
    <T extends Task> Optional<T> getTaskById(int id);

    // Create task
    void createTask(Task task);
    void createSubtask(Subtask subtask, Epic epic);
    void createEpic(Epic epic);

    // Update tasks
    void updateTask(Task task);
    void updateSubtask(Subtask subtask);
    void updateEpic(Epic epic);

    // Delete task by id
    void deleteTaskById(int id);

    // List subtasks by epic moved to Epic
    List<Subtask> getSubtasksByEpic(Epic epic);

    // update Epic status by its current Subtasks
    void updateEpicStatusById(int id);
}
