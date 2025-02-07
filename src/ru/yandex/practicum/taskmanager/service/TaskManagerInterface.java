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

    // Get tasks by id
    Optional<Task> getTaskById(int id);

    Optional<Subtask> getSubtaskById(int id);

    Optional<Epic> getEpicById(int id);

    // "Create" tasks inside Taskmanager
    int addTask(Task task);

    int addSubtask(Subtask subtask, Epic epic);

    int addEpic(Epic epic);

    // Update tasks
    void updateTask(Task task);

    void updateSubtask(Subtask subtask);

    void updateEpic(Epic epic);

    // Delete task by id
    void deleteTaskById(int id);

    void deleteSubtaskById(int id);

    void deleteEpicById(int id);

    // List subtasks by epic
    List<Subtask> getSubtasksByEpic(Epic epic);

    // update Epic status by id regarding its Subtasks current statuses
    void updateEpicStatusById(int id);
}
