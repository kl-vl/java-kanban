package ru.yandex.practicum;

import ru.yandex.practicum.taskmanager.model.Epic;
import ru.yandex.practicum.taskmanager.model.Status;
import ru.yandex.practicum.taskmanager.model.Subtask;
import ru.yandex.practicum.taskmanager.model.Task;
import ru.yandex.practicum.taskmanager.service.Managers;
import ru.yandex.practicum.taskmanager.service.TaskManager;

import java.util.Optional;

public class Main {

    private static final TaskManager manager = Managers.getDefault();
    private static int subtask4Id;
    private static int subtask5Id;
    private static int epic3Id;

    public static void main(String[] args) {
        runTests();
    }

    public static void runTests() {
        testAddTwoTasks();
        testAddEpicWithTwoSubtasks();
        testAddEpicWithOneSubtask();
        testUpdateSubtasks();
        testDeleteTasks();
    }

    public static void testAddTwoTasks() {
        System.out.println("+ Adding two Tasks:");
        Task task1 = new Task("Task 1", "Description of Task 1");
        Task task2 = new Task("Task 2", "Description of Task 2");

        //int task1Id =
        manager.addTask(task1);
        //int task2Id =
        manager.addTask(task2);

        System.out.println("= TaskManager's tasks after adding tasks " + task1.getName() + " and " + task2.getName());
        printAllTasks();
    }

    public static void testAddEpicWithTwoSubtasks() {
        System.out.println("\n+ Adding Epic");
        Epic epic3 = new Epic("Epic 1", "Description of Epic 1");
        epic3Id = manager.addEpic(epic3);

        Optional<Epic> oEpic3 = manager.getEpicById(epic3Id);
        if (oEpic3.isPresent()) {
            Epic retrievedEpic3 = oEpic3.get();

            System.out.println("+ Adding Subtasks to " + epic3.getName());

            Subtask subtask4 = new Subtask("Subtask 11", "Description of Subtask 11");
            Subtask subtask5 = new Subtask("Subtask 12", "Description of Subtask 12");

            subtask4Id = manager.addSubtask(subtask4, retrievedEpic3);
            subtask5Id = manager.addSubtask(subtask5, retrievedEpic3);

            printAllTasks();
        }
    }

    public static void testAddEpicWithOneSubtask() {
        System.out.println("\n+ Adding Epic 2 with Subtask");
        Epic epic6 = new Epic("Epic 2", "Description of Epic 2");
        Subtask subtask7 = new Subtask("Subtask 23", "Description of Subtask 23");

        int epic6Id = manager.addEpic(epic6);
        //int subtask7Id;

        Optional<Epic> oEpic6 = manager.getEpicById(epic6Id);
        if (oEpic6.isPresent()) {
            Epic retrievedEpic6 = oEpic6.get();

            manager.addSubtask(subtask7, retrievedEpic6);
            printAllTasks();

            testAddIllegalTaskWithId(subtask7, retrievedEpic6);
        }
    }

    public static void testAddIllegalTaskWithId(Subtask subtask7, Epic epic6) {

        System.out.println("\n+ Trying to add new Subtask" + subtask7.getName() + " with same id to " + epic6.getName());
        subtask7.setId(7);
        System.out.println(subtask7);
        //int subtask7Id =
                manager.addSubtask(subtask7, epic6);

        System.out.println("= TaskManager's subtasks after adding new Subtask with same id to Epic 2");
        for (Subtask subtask : manager.getSubtasks()) {
            System.out.println(subtask);
        }

        System.out.println("\n+ Changing " + epic6.getName() + " status to DONE outside TaskManager");

        epic6.setStatus(Status.DONE);
        manager.updateEpic(epic6);

        printAllTasks();
    }

    public static void testUpdateSubtasks() {

        // update Subtask with id = 4
        System.out.println("\n+ Changing Subtask = 4  status to IN_PROGRESS");

        Optional<Subtask> oSubtask11 = manager.getSubtaskById(subtask4Id);
        Optional<Subtask> oSubtask22 = manager.getSubtaskById(subtask5Id);
        Optional<Epic> oEpic31 = manager.getEpicById(epic3Id);
        if (oSubtask11.isEmpty() || oSubtask22.isEmpty() || oEpic31.isEmpty()) {
            return;
        }
        Subtask subtask11 = oSubtask11.get();
        Subtask subtask22 = oSubtask22.get();
        Epic epic31 = oEpic31.get();
        subtask11.setStatus(Status.IN_PROGRESS);
        subtask11.setName(subtask11.getName() + " edited");
        subtask11.setDescription(subtask11.getDescription() + " edited");

        manager.updateSubtask(subtask11);

//        System.out.println("= " + epic31.getName() + " status after updating Subtask = 4 to IN_PROGRESS:");
//        manager.getTaskById(epic3Id).ifPresent(task -> System.out.println(task));

        // update Subtask with id = 4,5
        System.out.println("\n+ Changing Subtasks = 4,5  status to DONE");

        subtask11.setStatus(Status.DONE);
        subtask22.setStatus(Status.DONE);

        manager.updateSubtask(subtask11);
        manager.updateSubtask(subtask22);

        // check epic status
        System.out.println("= " + epic31.getName() + " status after updating Subtask 4,5 to DONE:");


//        manager.getTaskById(epic3Id).ifPresent(task -> System.out.println(task));
        printAllTasks();

    }

    public static void testDeleteTasks() {

        Optional<Subtask> oSubtask11 = manager.getSubtaskById(4);
        Optional<Subtask> oSubtask22 = manager.getSubtaskById(5);
        Optional<Epic> oEpic31 = manager.getEpicById(3);
        if (oSubtask11.isEmpty() || oSubtask22.isEmpty() || oEpic31.isEmpty()) {
            return;
        }
        Subtask subtask11 = oSubtask11.get();
        Subtask subtask22 = oSubtask11.get();
        Epic epic3 = oEpic31.get();

        System.out.println("\n - Deleting by id subtasks " + subtask11.getName() + " and " + subtask22.getName() + " from " + epic3.getName());
        manager.deleteTaskById(subtask11.getId());
        manager.deleteTaskById(subtask22.getId());

        printAllTasks();

        System.out.println("\n- Deleting all Subtasks");
        manager.deleteSubtasks();
        System.out.println("= TaskManager' items after Deleting all Subtasks");

        printAllTasks();

        System.out.println("\n - Deleting all Tasks and Epics");
        manager.deleteEpics();
        manager.deleteTasks();
        System.out.println("= TaskManager' items after Deleting all Tasks and Epics");
        System.out.println("Tasks empty = " + manager.getTasks().isEmpty());
        System.out.println("Subtasks empty = " + manager.getSubtasks().isEmpty());
        System.out.println("Epics empty = " + manager.getEpics().isEmpty());

    }


    private static void printAllTasks() {
        System.out.println("Tasks:");
        for (Task task : Main.manager.getTasks()) {
            System.out.println(task);
        }
        System.out.println("Epics:");
        for (Epic epic : Main.manager.getEpics()) {
            System.out.println(epic);

            for (Subtask subtask : epic.getSubtasksList()) {
                System.out.println("--> " + subtask);
            }
        }
        System.out.println("Subtasks:");
        for (Task subtask : Main.manager.getSubtasks()) {
            System.out.println(subtask);
        }

        System.out.println("History:");
        for (Task task : Main.manager.getHistory()) {
            System.out.println(task);
        }
    }

}
