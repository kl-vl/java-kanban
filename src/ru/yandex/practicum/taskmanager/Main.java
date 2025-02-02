package ru.yandex.practicum.taskmanager;

import ru.yandex.practicum.taskmanager.model.Epic;
import ru.yandex.practicum.taskmanager.model.Subtask;
import ru.yandex.practicum.taskmanager.model.Task;
import ru.yandex.practicum.taskmanager.model.TaskStatus;
import ru.yandex.practicum.taskmanager.service.TaskManager;
import ru.yandex.practicum.taskmanager.service.TaskManagerInterface;

public class Main {

    public static void main(String[] args) {

        TaskManagerInterface manager = new TaskManager();

        // create two Tasks
        System.out.println("Adding two Tasks");

        Task task1 = new Task("Task 1", "Description of Task 1");
        Task task2 = new Task("Task 2", "Description of Task 2");
        manager.createTask(task1);
        manager.createTask(task2);

        for (Task task : manager.getTasks()) {
            System.out.println(task);
        }
        // create Epic
        System.out.println("Adding one Epic without Subtasks");
        Epic epic1 = new Epic("Epic 1", "Description of Epic 1");
        manager.createEpic(epic1);

        for (Epic epic : manager.getEpics()) {
            System.out.println(epic);
        }
        // create two Subtasks for first Epic
        System.out.println("Adding two Subtasks to Epic 1");
        Subtask subtask1 = new Subtask("Subtask 11", "Description of Subtask 11", epic1.getId());
        Subtask subtask2 = new Subtask("Subtask 12", "Description of Subtask 12", epic1.getId());
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        System.out.println(epic1);
        for (Subtask subtask : manager.getSubtasks()) {
            System.out.println(subtask);
        }

        // create second Epic
        System.out.println("Adding second Epic 2");
        Epic epic2 = new Epic("Epic 2", "Description of Epic 2");
        manager.createEpic(epic2);

        for (Epic epic : manager.getEpics()) {
            System.out.println(epic);
        }


        // create Subtask for second Epic
        System.out.println("Adding Subtask to Epic 2");
        Subtask subtask3 = new Subtask("Subtask 23", "Description of Subtask 23", epic2.getId());
        manager.createSubtask(subtask3);
        System.out.println(epic2);
        for (Subtask subtask : manager.getSubtasksByEpic(epic2)) {
            System.out.println(subtask);
        }

        epic1.setStatus(TaskStatus.DONE);

        // update subtask status
        subtask1.setStatus(TaskStatus.DONE);
        manager.updateSubtask(subtask1);
        // check epic status
        System.out.println("Epic 1 status after updating Subtask 11 to DONE: " + epic1.getStatus());

        // update subtask status
        subtask2.setStatus(TaskStatus.DONE);
        manager.updateSubtask(subtask2);
        // check epic status
        System.out.println("Epic 2 status after updating Subtask 12 to DONE: " + epic1.getStatus());
        System.out.println(epic1);
        for (Subtask subtask : manager.getSubtasksByEpic(epic1)) {
            System.out.println(subtask);
        }

        System.out.println("Deleting Subtask 11 from Epic 1");
        manager.deleteTaskById(subtask1.getId());

        for (Epic epic : manager.getEpics()) {
            System.out.println(epic);
        }
        for (Subtask subtask : manager.getSubtasks()) {
            System.out.println(subtask);
        }

        System.out.println("Deleting all Subtasks");
        manager.deleteSubtasks();

        for (Task task : manager.getTasks()) {
            System.out.println(task);
        }

        System.out.println("Deleting all Tasks");
        manager.deleteTasks();


        //System.out.println(manager.getSubtasks());
    }
}
