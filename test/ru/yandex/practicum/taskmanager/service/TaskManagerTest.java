package ru.yandex.practicum.taskmanager.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.taskmanager.model.Epic;
import ru.yandex.practicum.taskmanager.model.Status;
import ru.yandex.practicum.taskmanager.model.Subtask;
import ru.yandex.practicum.taskmanager.model.Task;
import ru.yandex.practicum.taskmanager.service.exception.InvalidManagerTaskException;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;

    protected abstract T createTaskManager() throws IOException;

    @BeforeEach
    void setUp() throws IOException {
        taskManager = createTaskManager();
    }

    @Test
    void testTaskAddedAndRetrievedCorrectly() throws InvalidManagerTaskException, NoSuchElementException {
        final LocalDateTime startTime = LocalDateTime.of(2025, 4, 8, 15, 47);
        final Duration duration = Duration.ofMinutes(31);
        final Task task1 = new Task("Task 1", "Description of Task 1", startTime, duration);
        final int task1Id = taskManager.addTask(task1);
        final boolean isId = (task1Id > 0);

        final Task copiedTask1 = task1.copy(task1Id);
        final Task savedTask1 = taskManager.getTaskById(task1Id).orElseThrow();

        assertAll("Added task should exist",
                () -> assertTrue(isId, "Added task should have positive id"),
                () -> assertEquals(copiedTask1, savedTask1, "Added task does not match saved task.")
        );


        final Task updatedTask1 = savedTask1.copyWith(savedTask1.getName() + " Updated", savedTask1.getDescription() + " Updated", Status.IN_PROGRESS, null, null);
        taskManager.updateTask(updatedTask1);
        final Task savedTask2 = taskManager.getTaskById(task1Id).orElseThrow();

        assertAll("Setters should properly update Task fields",
                () -> assertEquals("Task 1 Updated", savedTask2.getName(), "The name of the task should be updated"),
                () -> assertEquals("Description of Task 1 Updated", savedTask2.getDescription(), "The description of the task should be updated"),
                () -> assertEquals(Status.IN_PROGRESS, savedTask2.getStatus(), "The status of the task should be updated to IN_PROGRESS")
        );


        final List<Task> tasks = taskManager.getTasks();
        final Task finalTask1 = taskManager.getTaskById(task1Id).orElseThrow();

        assertAll("Manager should have correct number of tasks after adding Task",
                () -> assertNotNull(tasks, "getTasks() should return empty list."),
                () -> assertEquals(1, tasks.size(), "The wrong number of tasks obtained by getTasks()."),
                () -> assertEquals(finalTask1, tasks.getFirst(), "Added Task does not match saved task obtained by getTasks().")
        );
    }

    @Test
    void testAddEpicAndRetrieveItCorrectly() throws InvalidManagerTaskException {
        Epic epic = new Epic("Epic 1", "Description of Epic 1");
        final int epicId = taskManager.addEpic(epic);
        final Epic epicWithId = epic.copy(epicId);

        final Optional<Epic> oEpic = taskManager.getEpicById(epicId);
        final List<Epic> epics = taskManager.getEpics();

        assertAll("Task manager should contain correct number of Epics",
                () -> assertTrue(oEpic.isPresent(), "Saved Epic does not exist in InMemoryTaskManager."),
                () -> assertEquals(epicWithId, oEpic.orElseThrow(() -> new AssertionError("Epic not found")), "Added Epic does not match saved Epic."),
                () -> assertNotNull(epics, "Empty list of Epics obtained by getTasks()."),
                () -> assertEquals(1, epics.size(), "The wrong number of Epics obtained by getEpics()."),
                () -> assertEquals(epicWithId, epics.getFirst(), "Added Epic does not match saved task obtained by getEpics().")
        );
    }

    @Test
    void testAddSubtasksToEpicAndRetrieveItCorrectly() throws InvalidManagerTaskException, NoSuchElementException {
        final Epic epicNew1 = new Epic("Epic 1", "Description of Epic 1");
        final int epicId = taskManager.addEpic(epicNew1);
        final Epic epic1 = epicNew1.copy(epicId);

        final LocalDateTime startTime1 = LocalDateTime.of(2025, 4, 8, 15, 47);
        final Duration duration1 = Duration.ofMinutes(31);
        final Subtask subtaskNew1 = new Subtask("Subtask 1", "Description of Subtask 1", startTime1, duration1);
        final int subtaskId1 = taskManager.addSubtask(subtaskNew1, epic1);
        final Subtask subtask1 = subtaskNew1.copy(subtaskId1);
        final Subtask subtaskSaved1 = taskManager.getSubtaskById(subtaskId1).orElseThrow();

        final LocalDateTime startTime2 = LocalDateTime.of(2025, 4, 8, 15, 47);
        final Duration duration2 = Duration.ofMinutes(59);
        final Subtask subtaskNew2 = new Subtask("Subtask 2", "Description of Subtask 2");
        final int subtaskId2 = taskManager.addSubtask(subtaskNew2, epic1);
        final Subtask subtask2 = subtaskNew2.copy(subtaskId2);
        final Subtask subtaskSaved2 = taskManager.getSubtaskById(subtaskId2).orElseThrow();

        final Epic epicSaved1 = taskManager.getEpicById(epicId).orElseThrow();
        final Epic epicBySubtask2 = taskManager.getEpicBySubtask(subtaskSaved2).orElseThrow();
        final List<Epic> epics = taskManager.getEpics();
        final List<Subtask> subtasks1 = epicSaved1.getSubtasksList();

        assertAll("Task manager should contain correct number of Epics and Subtasks",
                () -> assertEquals(epic1, epicSaved1, "Added Epic does not match saved epic."),
                () -> assertNotNull(epics, "Empty list of epics obtained by getTasks()."),
                () -> assertEquals(1, epics.size(), "The wrong number of epics obtained by getEpics()."),
                () -> assertEquals(epicSaved1, epics.getFirst(), "Added Epic does not match saved task obtained by getEpics()."),
                () -> assertEquals(subtask1, subtaskSaved1, "Added Subtask 1 does not match saved Subtask."),
                () -> assertEquals(subtask2, subtaskSaved2, "Added Subtask 2 does not match saved Subtask."),
                () -> assertEquals(List.of(subtaskSaved1, subtaskSaved2), subtasks1, "Subtasks should match the subtasks list"),
                () -> assertEquals(taskManager.getSubtasksByEpicId(epicId), subtasks1, "Subtasks retrieved by epic should match the subtasks list in the epic"),
                () -> assertEquals(2, subtasks1.size(), "Epic should contain 2 subtasks after adding two subtasks"),
                () -> assertTrue(subtasks1.contains(subtask1), "Epic's subtasks list should contain subtask1"),
                () -> assertTrue(subtasks1.contains(subtask2), "Epic's subtasks list should contain subtask2")
        );

        taskManager.deleteSubtaskById(subtaskId2);
        Epic epicSaved2 = taskManager.getEpicById(epicId).orElseThrow();
        final List<Subtask> subtasks2 = epicSaved2.getSubtasksList();

        assertAll("Epic subtasks list should contain correct number of subtasks after delete subtask",
                () -> assertEquals(1, subtasks2.size(), "Epic should contain 1 subtask after deleting one subtask"),
                () -> assertTrue(subtasks2.contains(subtask1), "Epic's subtasks list should still contain subtask1 after deleting subtask2")
        );
    }


    //    Для расчёта статуса Epic. Граничные условия:
//    a. Все подзадачи со статусом NEW.
//    b. Все подзадачи со статусом DONE.
//    c. Подзадачи со статусами NEW и DONE.
//    d. Подзадачи со статусом IN_PROGRESS.
    @Test
    void testEpicStatusAfterAdd() throws InvalidManagerTaskException {
        final Epic epic = new Epic("Test Epic", "Test Description");
        final int epicId = taskManager.addEpic(epic);

        final Epic savedEpic = taskManager.getEpicById(epicId).orElseThrow();

        assertEquals(Status.NEW, savedEpic.getStatus(), "Epic status should be NEW when no subtasks are present");
    }

    @Test
    void testEpicUpdateStatusNew() throws InvalidManagerTaskException {
        final Epic epic = new Epic("Test Epic", "Test Description");
        final int epicId = taskManager.addEpic(epic);
        final Subtask subtask1 = new Subtask("Test Subtask 1", "Test Subtask Description 1");
        final Subtask subtask2 = new Subtask("Test Subtask 2", "Test Subtask Description 2");

        // NEW
        Epic savedEpic = taskManager.getEpicById(epicId).orElseThrow();
        final int subtaskId1 = taskManager.addSubtask(subtask1, savedEpic);
        final int subtaskId2 = taskManager.addSubtask(subtask2, savedEpic);
        savedEpic = taskManager.getEpicById(epicId).orElseThrow();

        assertEquals(Status.NEW, savedEpic.getStatus(), "Epic status should be NEW when Subtasks in NEW status");
    }

    @Test
    void testEpicUpdateStatusNewDone() throws InvalidManagerTaskException {
        final Epic epic = new Epic("Test Epic", "Test Description");
        final int epicId = taskManager.addEpic(epic);
        final Subtask subtask1 = new Subtask("Test Subtask 1", "Test Subtask Description 1");
        final Subtask subtask2 = new Subtask("Test Subtask 2", "Test Subtask Description 2");

        Epic savedEpic = taskManager.getEpicById(epicId).orElseThrow();
        final int subtaskId1 = taskManager.addSubtask(subtask1, savedEpic);
        final int subtaskId2 = taskManager.addSubtask(subtask2, savedEpic);

        //NEW & DONE
        Subtask savedSubtask1 = taskManager.getSubtaskById(subtaskId1).orElseThrow();
        savedSubtask1.setStatus(Status.DONE);
        taskManager.updateSubtask(savedSubtask1);

        savedEpic = taskManager.getEpicById(epicId).orElseThrow();

        assertEquals(Status.IN_PROGRESS, savedEpic.getStatus(), "Epic status should be IN_PROGRESS when one subtask is DONE and the other is NEW");
    }

    @Test
    void testEpicUpdateStatusDone() throws InvalidManagerTaskException {
        final Epic epic = new Epic("Test Epic", "Test Description");
        final int epicId = taskManager.addEpic(epic);
        final Subtask subtask1 = new Subtask("Test Subtask 1", "Test Subtask Description 1");
        final Subtask subtask2 = new Subtask("Test Subtask 2", "Test Subtask Description 2");

        Epic savedEpic = taskManager.getEpicById(epicId).orElseThrow();
        final int subtaskId1 = taskManager.addSubtask(subtask1, savedEpic);
        final int subtaskId2 = taskManager.addSubtask(subtask2, savedEpic);

        // DONE
        Subtask savedSubtask1 = taskManager.getSubtaskById(subtaskId1).orElseThrow();
        savedSubtask1.setStatus(Status.DONE);
        taskManager.updateSubtask(savedSubtask1);
        Subtask savedSubtask2 = taskManager.getSubtaskById(subtaskId2).orElseThrow();
        savedSubtask2.setStatus(Status.DONE);
        taskManager.updateSubtask(savedSubtask2);
        savedEpic = taskManager.getEpicById(epicId).orElseThrow();

        assertEquals(Status.DONE, savedEpic.getStatus(), "Epic status should be DONE when all subtask is DONE");
    }

    @Test
    void testEpicUpdateStatusInProgress() throws InvalidManagerTaskException {
        final Epic epic = new Epic("Test Epic", "Test Description");
        final int epicId = taskManager.addEpic(epic);
        final Subtask subtask1 = new Subtask("Test Subtask 1", "Test Subtask Description 1");
        final Subtask subtask2 = new Subtask("Test Subtask 2", "Test Subtask Description 2");

        Epic savedEpic = taskManager.getEpicById(epicId).orElseThrow();
        final int subtaskId1 = taskManager.addSubtask(subtask1, savedEpic);
        final int subtaskId2 = taskManager.addSubtask(subtask2, savedEpic);

        // IN_PROGRESS
        Subtask savedSubtask1 = taskManager.getSubtaskById(subtaskId1).orElseThrow();
        savedSubtask1.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(savedSubtask1);
        Subtask savedSubtask2 = taskManager.getSubtaskById(subtaskId1).orElseThrow();
        savedSubtask2.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(savedSubtask2);
        savedEpic = taskManager.getEpicById(epicId).orElseThrow();

        assertEquals(Status.IN_PROGRESS, savedEpic.getStatus(), "Epic status should be IN_PROGRESS when all IN_PROGRESS");
    }

    @Test
    void testGetByIdWithIncorrectIdShouldReturnEmpty() {
        assertAll(
                () -> assertTrue(taskManager.getTaskById(123).isEmpty(),
                        "Expected no task to be found with ID 123, but a task was returned."),
                () -> assertTrue(taskManager.getSubtaskById(456).isEmpty(),
                        "Expected no subtask to be found with ID 456, but a subtask was returned."),
                () -> assertTrue(taskManager.getEpicById(789).isEmpty(),
                        "Expected no epic to be found with ID 789, but a epic was returned.")
        );
    }

    @Test
    void testDeleteTypeOfTaskShouldWorkCorrectly() throws InvalidManagerTaskException {
        final Task task1 = new Task("Task 1", "Description of Task 1");
        final Task task2 = new Task("Task 2", "Description of Task 2");
        taskManager.addTask(task1);
        final int taskId2 = taskManager.addTask(task2);

        Epic epic1 = new Epic("Epic 1", "Description of Epic 1");
        Epic epic2 = new Epic("Epic 2", "Description of Epic 2");
        final int epicId1 = taskManager.addEpic(epic1);
        final int epicId2 = taskManager.addEpic(epic2);
        epic1 = taskManager.getEpicById(epicId1).orElseThrow();
        epic2 = taskManager.getEpicById(epicId2).orElseThrow();

        Subtask subtask1 = new Subtask("Subtask 1", "Description of Subtask 1");
        Subtask subtask2 = new Subtask("Subtask 2", "Description of Subtask 2");
        final int subtaskId1 = taskManager.addSubtask(subtask1, epic1);
        final int subtaskId2 = taskManager.addSubtask(subtask2, epic2);

        final List<Task> taskList = taskManager.getTasks();
        final List<Subtask> subtaskList = taskManager.getSubtasks();
        final List<Epic> epicList = taskManager.getEpics();

        assertAll("Lists of different types of task should contain correct number of tasks after adding",
                () -> assertEquals(2, taskList.size(), "Expected 2 tasks, but found " + taskList.size() + " tasks."),
                () -> assertEquals(2, subtaskList.size(), "Expected 2 subtasks, but found " + subtaskList.size() + " subtasks."),
                () -> assertEquals(2, epicList.size(), "Expected 2 epics, but found " + epicList.size() + " epics.")
        );

        taskManager.deleteTaskById(taskId2);
        taskManager.deleteSubtaskById(subtaskId2);
        taskManager.deleteEpicById(epicId2);
        final List<Task> taskList2 = taskManager.getTasks();
        final List<Subtask> subtaskList2 = taskManager.getSubtasks();
        final List<Epic> epicList2 = taskManager.getEpics();

        assertAll("Lists of different types of task should contain correct number of tasks after deletion",
                () -> assertEquals(1, taskList2.size(), "Expected 1 task after deletion, but found " + taskList.size() + " tasks."),
                () -> assertEquals(1, subtaskList2.size(), "Expected 1 subtask after deletion, but found " + subtaskList.size() + " subtasks."),
                () -> assertEquals(1, epicList2.size(), "Expected 1 epic after deletion, but found " + epicList.size() + " epics.")
        );

        taskManager.deleteTasks();
        taskManager.deleteSubtasks();
        taskManager.deleteEpics();
        final List<Task> taskList3 = taskManager.getTasks();
        final List<Subtask> subtaskList3 = taskManager.getSubtasks();
        final List<Epic> epicList3 = taskManager.getEpics();

        assertAll("Lists of different types of task should be empty after deletion",
                () -> assertTrue(taskList3.isEmpty(), "Expected no tasks to be deleted, but found " + taskList.size() + " tasks."),
                () -> assertTrue(subtaskList3.isEmpty(), "Expected subtask list to be empty after deletion, but it contains " + subtaskList.size() + " subtasks."),
                () -> assertTrue(epicList3.isEmpty(), "Expected epic list to be empty after deletion, but it contains " + epicList.size() + " epics.")
        );
    }

    /**
     * Additional test (Sprint 6) instead of User scenario in Main.java:
     * * <ul>
     * * <li>Create two tasks, an epic with three subtasks, and an epic with no subtasks.</li>
     * * <li>Request the created tasks multiple times in different orders.</li>
     * * <li>After each request, print the history and ensure there are no duplicates.</li>
     * * <li>Delete a task that is present in the history and verify that it no longer appears when printing.</li>
     * * <li>Delete the epic with three subtasks and ensure that both the epic and all its subtasks are removed from the history.</li>
     * * </ul>
     */
    @Test
    void testUserScenarioSprint6ShouldWorkCorrectly() throws InvalidManagerTaskException {
        Task task1 = new Task("Task 1", "Description of Task 1");
        Task task2 = new Task("Task 2", "Description of Task 2");
        int taskId1 = taskManager.addTask(task1);
        int taskId2 = taskManager.addTask(task2);
        task1 = taskManager.getTaskById(taskId1).orElseThrow();
        task2 = taskManager.getTaskById(taskId2).orElseThrow();

        Epic epic1 = new Epic("Epic 1", "Description of Epic 1");
        Epic epic2 = new Epic("Epic 2", "Description of Epic 2");
        int epicId1 = taskManager.addEpic(epic1);
        int epicId2 = taskManager.addEpic(epic2);

        epic1 = taskManager.getEpicById(epicId1).orElseThrow();
        epic2 = taskManager.getEpicById(epicId2).orElseThrow();

        Subtask subtask1 = new Subtask("Subtask 1", "Description of Subtask 1");
        Subtask subtask2 = new Subtask("Subtask 2", "Description of Subtask 2");
        Subtask subtask3 = new Subtask("Subtask 3", "Description of Subtask 3");
        int subtaskId1 = taskManager.addSubtask(subtask1, epic1);
        int subtaskId2 = taskManager.addSubtask(subtask2, epic1);
        int subtaskId3 = taskManager.addSubtask(subtask3, epic1);
        subtask1 = taskManager.getSubtaskById(subtaskId1).orElseThrow();
        subtask2 = taskManager.getSubtaskById(subtaskId2).orElseThrow();
        subtask3 = taskManager.getSubtaskById(subtaskId3).orElseThrow();

        taskManager.getTaskById(taskId1);
        taskManager.getTaskById(taskId2);
        taskManager.getSubtaskById(subtaskId1);
        taskManager.getSubtaskById(subtaskId2);
        taskManager.getSubtaskById(subtaskId3);
        taskManager.getEpicById(epicId1);
        taskManager.getEpicById(epicId2);
        taskManager.getTaskById(taskId2);
        taskManager.getTaskById(taskId1);
        taskManager.getSubtaskById(subtaskId3);
        taskManager.getSubtaskById(subtaskId2);
        taskManager.getSubtaskById(subtaskId1);
        taskManager.getEpicById(epicId2);
        taskManager.getEpicById(epicId1);
        final List<Task> historyList1 = taskManager.getHistory();
        final boolean containAllAddedTasks = historyList1.containsAll(List.of(task1, task2, epic1, epic2, subtask1, subtask2, subtask3));

        assertAll("Manager's history should contain all the added tasks",
                () -> assertEquals(7, historyList1.size(), "History list should contain 7 elements"),
                () -> assertTrue(containAllAddedTasks, "History list should contain all viewed tasks, epics, and subtasks\"")
        );

        taskManager.deleteTaskById(taskId2);
        final List<Task> historyList2 = taskManager.getHistory();
        final boolean containsTask2 = historyList2.contains(task2);

        assertAll("Manager should contain correct number of tasks after Task deletion",
                () -> assertEquals(6, historyList2.size(), "History list should contain 7 elements"),
                () -> assertFalse(containsTask2, "History list should not contain Task 2 after deletion")
        );

        taskManager.deleteEpicById(epicId1);
        final List<Task> historyList3 = taskManager.getHistory();
        final boolean containsNoSubtasks = historyList3.stream().noneMatch(element -> element instanceof Subtask);
        final boolean containsTask1 = historyList3.contains(task1);
        final boolean containsEpic3 = historyList3.contains(epic2);

        assertAll("Manager should contain correct number of tasks after Epic deletion",
                () -> assertTrue(containsNoSubtasks, "History list should not contain any subtasks after deleting Epic 1"),
                () -> assertEquals(2, historyList3.size(), "History list should contain 2 elements after deleting Epic 1 and its subtasks"),
                () -> assertTrue(containsTask1, "History list should still contain Task 1"),
                () -> assertTrue(containsEpic3, "History list should still contain Epic 2")
        );
    }

    @Test
    void testClearAll() throws InvalidManagerTaskException {
        Task task = new Task("Task 1", "Description of Task 1");
        taskManager.addTask(task);

        Epic epic = new Epic("Epic 1", "Description of Epic 1");
        final int epicId = taskManager.addEpic(epic);
        epic = epic.copy(epicId);

        final Subtask subtask = new Subtask("Subtask 1", "Description of Subtask 1");
        taskManager.addSubtask(subtask, epic);

        taskManager.deleteTasks();
        taskManager.deleteEpics();
        taskManager.deleteSubtasks();

        assertAll("All task collections should be empty after clearAll",
                () -> assertTrue(taskManager.getTasks().isEmpty(), "Tasks should be empty"),
                () -> assertTrue(taskManager.getSubtasks().isEmpty(), "Subtasks should be empty"),
                () -> assertTrue(taskManager.getEpics().isEmpty(), "Epics should be empty"),
                () -> assertTrue(taskManager.getHistory().isEmpty(), "History should be empty")
        );
    }

    @Test
    void testGetSubtasksByEpic() throws InvalidManagerTaskException {
        Epic epic = new Epic("Epic 1", "Description of Epic 1");
        int epicId = taskManager.addEpic(epic);
        final Epic epicSaved = taskManager.getEpicById(epicId).orElseThrow(() -> new AssertionError("Epic not found"));

        final Subtask subtaskNew1 = new Subtask("Subtask 1", "Description of Subtask 1");
        final Subtask subtaskNew2 = new Subtask("Subtask 2", "Description of Subtask 2");
        int subtaskId1 = taskManager.addSubtask(subtaskNew1, epicSaved);
        int subtaskId2 = taskManager.addSubtask(subtaskNew2, epicSaved);
        final Subtask subtask1 = subtaskNew1.copy(subtaskId1);
        final Subtask subtask2 = subtaskNew2.copy(subtaskId2);

        List<Subtask> subtasks = taskManager.getSubtasksByEpicId(epicId);

        assertAll("Subtasks should match the ones added to the epic",
                () -> assertEquals(2, subtasks.size(), "Epic should have 2 subtasks"),
                () -> assertTrue(subtasks.contains(subtask1), "Subtask 1 should be in the list"),
                () -> assertTrue(subtasks.contains(subtask2), "Subtask 2 should be in the list")
        );
    }

    @Test
    void testPrioritizedTasksListWorkCorrectly() throws InvalidManagerTaskException {
        final LocalDateTime startTime1 = LocalDateTime.of(2025, 4, 8, 10, 01);
        final Duration duration1 = Duration.ofMinutes(31);
        final Task task1 = new Task("Task 1", "Description of Task 1", startTime1, duration1);
        final int task1Id = taskManager.addTask(task1);

        final LocalDateTime startTime2 = LocalDateTime.of(2025, 4, 8, 8, 0);
        final Duration duration2 = Duration.ofMinutes(25);
        final Task task2 = new Task("Task 2", "Description of Task 2", startTime2, duration2);
        final int task2Id = taskManager.addTask(task2);

        final LocalDateTime startTime3 = LocalDateTime.of(2025, 4, 8, 9, 15);
        final Duration duration3 = Duration.ofMinutes(17);
        final Task task3 = new Task("Task 3", "Description of Task 3", startTime3, duration3);
        final int task3Id = taskManager.addTask(task3);

        final Task task4 = new Task("Task 4", "Description of Task 4");
        final int task4Id = taskManager.addTask(task4);

        final List<Task> tasks = taskManager.getPrioritizedTasks();

        assertAll(
                () -> assertEquals(3, tasks.size(), "Prioritized task list should have 3 items"),
                () -> assertEquals(task2Id, tasks.getFirst().getId(), "Task 1 should have the task 1 id"),
                () -> assertEquals(task1Id, tasks.getLast().getId(), "Task 1 should have the task 1 id")
        );
    }

    @Test
    void testIntersectionOfTasksByTime() throws InvalidManagerTaskException {
        final LocalDateTime startTime1 = LocalDateTime.of(2025, 4, 8, 10, 9);
        final Duration duration1 = Duration.ofMinutes(31);
        final Task task1 = new Task("Task 1", "Description of Task 1", startTime1, duration1);
        final int task1Id = taskManager.addTask(task1);

        final LocalDateTime startTime2 = LocalDateTime.of(2025, 4, 8, 10, 40);
        final Duration duration2 = Duration.ofMinutes(25);
        final Task task2 = new Task("Task 2", "Description of Task 2", startTime2, duration2);
        final String expectedMessage = "has intersection with managers tasks";

        final InvalidManagerTaskException exception2 = assertThrows(
                InvalidManagerTaskException.class,
                () -> taskManager.addTask(task2)
        );

        assertAll(
            () -> assertTrue(exception2.getMessage().contains(expectedMessage), "Exception message should indicate intersection of tasks."),
            () -> assertEquals(1,taskManager.getPrioritizedTasks().size(), "Prioritized task list should have 1 task")
        );

        final LocalDateTime startTime3 = LocalDateTime.of(2025, 4, 8, 10, 0);
        final Duration duration3 = Duration.ofMinutes(9);
        final Task task3 = new Task("Task 3", "Description of Task 3", startTime3, duration3);

        final InvalidManagerTaskException exception3 = assertThrows(
                InvalidManagerTaskException.class,
                () -> taskManager.addTask(task3)
        );

        assertAll(
                () -> assertTrue(exception3.getMessage().contains(expectedMessage), "Exception message should indicate intersection of tasks."),
                () -> assertEquals(1,taskManager.getPrioritizedTasks().size(), "Prioritized task list should have 1 task")
        );
    }

}
