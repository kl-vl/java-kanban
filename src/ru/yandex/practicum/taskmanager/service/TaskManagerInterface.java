package ru.yandex.practicum.taskmanager.service;

import ru.yandex.practicum.taskmanager.model.Epic;
import ru.yandex.practicum.taskmanager.model.Subtask;
import ru.yandex.practicum.taskmanager.model.Task;

import java.util.List;

public interface TaskManagerInterface {

    // List of tasks
    // TODO  a. Получение списка всех задач.
    List<Task> getTasks();
    List<Subtask> getSubtasks();
    List<Epic> getEpics();

    // Delete tasks
    // TODO   b. Удаление всех задач.
    void deleteTasks();
    void deleteSubtasks();
    void deleteEpics();

    // Get task by id
    // TODO    c. Получение по идентификатору.
    <T extends Task> T getTaskById(int id);
    //Subtask getSubtaskById(int id);
    //Epic getEpicById(int id);

    // Create task
    //    d. Создание. Сам объект должен передаваться в качестве параметра.
    void createTask(Task task);
    void createSubtask(Subtask subtask, Epic epic);
    void createEpic(Epic epic);

    // Update task
    // TODO    e. Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    void updateTask(Task task);
    void updateSubtask(Subtask subtask);
    void updateEpic(Epic epic);

    // Delete task by id
    // TODO  f. Удаление по идентификатору.
    void deleteTaskById(int id);
    //void deleteSubtaskById(int id);
    //void deleteEpicById(int id);
    //void deleteTaskById(int id);

    // List subtasks by epic moved to Epic
    // TODO    a. Получение списка всех подзадач определённого эпика.
    List<Subtask> getSubtasksByEpic(Epic epic);
}
