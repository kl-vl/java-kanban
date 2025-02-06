package ru.yandex.practicum;

import ru.yandex.practicum.taskmanager.model.Epic;
import ru.yandex.practicum.taskmanager.model.Subtask;
import ru.yandex.practicum.taskmanager.model.Task;
import ru.yandex.practicum.taskmanager.model.TaskStatus;
import ru.yandex.practicum.taskmanager.service.TaskManager;
import ru.yandex.practicum.taskmanager.service.TaskManagerInterface;

import java.util.Optional;

public class Main {

    public static void main(String[] args) {

        TaskManagerInterface manager = new TaskManager();
        // =====
        // create two Tasks
        Task task1 = new Task("Task 1", "Description of Task 1");
        Task task2 = new Task("Task 2", "Description of Task 2");

        System.out.println("+ Adding two Tasks:");
        System.out.println(task1);
        System.out.println(task2);

        manager.createTask(task1);
        manager.createTask(task2);
        // just to test task update
        manager.updateTask(task1);

        System.out.println("= TaskManager's tasks after adding tasks " + task1.getName() + " and " + task2.getName());
        for (Task task : manager.getTasks()) {
            System.out.println(task);
        }

        // =====
        // create Epic
        System.out.println("\n+ Adding Epic");
        Epic epic3 = new Epic("Epic 1", "Description of Epic 1");
        System.out.println(epic3);

        manager.createEpic(epic3);

        System.out.println("= TaskManager's epics after adding " + epic3.getName());
        for (Epic epic : manager.getEpics()) {
            System.out.println(epic);
        }

        // =====
        // create two Subtasks for Epic 1
        System.out.println("\n+ Adding Subtasks to " + epic3.getName());
        Subtask subtask4 = new Subtask("Subtask 11", "Description of Subtask 11");
        Subtask subtask5 = new Subtask("Subtask 12", "Description of Subtask 12");

        System.out.println(epic3);
        System.out.println(subtask4);
        System.out.println(subtask5);

        manager.createSubtask(subtask4, epic3);
        manager.createSubtask(subtask5, epic3);

        System.out.println("= TaskManager's after adding two Subtasks" + subtask4.getName() + "and " + subtask5.getName() + " to " + epic3.getName());
        manager.getTaskById(epic3.getId()).ifPresent(task -> System.out.println(task));
        for (Subtask subtask : manager.getSubtasks()) {
            System.out.println(subtask);
        }

        // =====
        // create second Epic
        System.out.println("\n+ Adding Epic 2");
        Epic epic6 = new Epic("Epic 2", "Description of Epic 2");
        System.out.println(epic6);

        manager.createEpic(epic6);

        System.out.println("= TaskManager's epics after adding second Epic 2");
        for (Epic epic : manager.getEpics()) {
            System.out.println(epic);
        }


        //=====
        // create Subtask for second Epic
        System.out.println("TaskManager's subtasks after adding Subtask to " + epic6.getName());
        Subtask subtask7 = new Subtask("Subtask 23", "Description of Subtask 23");
        manager.createSubtask(subtask7, epic6);
        for (Epic epic : manager.getEpics()) {
            System.out.println(epic);
        }
        for (Subtask subtask : manager.getSubtasksByEpic(epic6)) {
            System.out.println(subtask);
        }

        // =====
        // illegal create of Subtask with id
        System.out.println("\n+ Trying to add new Subtask" + subtask7.getName() + " with same id to " + epic6.getName());
        System.out.println(subtask7);
        manager.createSubtask(subtask7, epic6);

        System.out.println("= TaskManager's subtasks after adding new Subtask with same id to Epic 2");
        for (Subtask subtask : manager.getSubtasks()) {
            System.out.println(subtask);
        }

        // =====
        // illegal change of Epic Status
        System.out.println("\n+ Changing " + epic3.getName() + " status to DONE outside TaskManager");

        epic3.setStatus(TaskStatus.DONE);
        manager.updateEpic(epic3);

        System.out.println(epic3);
        System.out.println("= TaskManager's epics after:");

        for (Epic epic : manager.getEpics()) {
            System.out.println(epic);
        }

        // =====
        // update Subtask with id = 4
        System.out.println("\n+ Changing Subtask = 4  status to IN_PROGRESS");

        Optional<Subtask> oSubtask11 = manager.getTaskById(subtask4.getId());
        Optional<Subtask> oSubtask22 = manager.getTaskById(subtask5.getId());
        if (oSubtask11.isPresent() && oSubtask22.isPresent()) {
            Subtask subtask11 = oSubtask11.get();
            Subtask subtask22 = oSubtask22.get();
            subtask11.setStatus(TaskStatus.IN_PROGRESS);
            subtask11.setName(subtask11.getName() + " edited");
            subtask11.setDescription(subtask11.getDescription() + " edited");

            System.out.println(subtask11);

            manager.updateSubtask(subtask11);
            manager.getTaskById(subtask11.getId()).ifPresent(task -> System.out.println(task));


            System.out.println("= " + epic3.getName() + " status after updating Subtask = 4 to IN_PROGRESS:");
            manager.getTaskById(epic3.getId()).ifPresent(task -> System.out.println(task));

            // update Subtask with id = 4,5
            System.out.println("\n+ Changing Subtasks = 4,5  status to DONE");

            subtask11.setStatus(TaskStatus.DONE);
            subtask22.setStatus(TaskStatus.DONE);

            System.out.println(subtask11);
            System.out.println(subtask22);

            // update subtask status
            manager.updateSubtask(subtask11);
            manager.updateSubtask(subtask22);

            // check epic status
            System.out.println("= " + epic3.getName() + " status after updating Subtask 4,5 to DONE:");
            manager.getTaskById(epic3.getId()).ifPresent(task -> System.out.println(task));
            for (Subtask subtask : manager.getSubtasksByEpic(epic3)) {
                System.out.println(subtask);
            }

            // =====
            System.out.println("\n - Deleting by id subtasks " + subtask11.getName() + " and " + subtask22.getName() + " from " + epic3.getName());
            manager.deleteTaskById(subtask11.getId());
            manager.deleteTaskById(subtask22.getId());
            for (Epic epic : manager.getEpics()) {
                System.out.println(epic);
            }
            for (Subtask subtask : manager.getSubtasks()) {
                System.out.println(subtask);
            }
        }
        // =====
        System.out.println("\n- Deleting all Subtasks");
        manager.deleteSubtasks();
        System.out.println("= TaskManager' items after Deleting all Subtasks");
        for (Task task : manager.getTasks()) {
            System.out.println(task);
        }
        for (Epic epic : manager.getEpics()) {
            System.out.println(epic);
        }

        System.out.println("\n - Deleting all Tasks and Epics");
        manager.deleteEpics();
        manager.deleteTasks();
        System.out.println("= TaskManager' items after Deleting all Tasks and Epics");
        System.out.println("Tasks empty = " + manager.getTasks().isEmpty());
        System.out.println("Subtasks empty = " + manager.getSubtasks().isEmpty());
        System.out.println("Epics empty = " + manager.getEpics().isEmpty());


    }


}
