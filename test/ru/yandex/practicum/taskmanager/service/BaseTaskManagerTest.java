package ru.yandex.practicum.taskmanager.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.taskmanager.model.Epic;
import ru.yandex.practicum.taskmanager.model.Status;
import ru.yandex.practicum.taskmanager.model.Subtask;
import ru.yandex.practicum.taskmanager.model.Task;
import ru.yandex.practicum.taskmanager.service.exception.InvalidManagerTaskException;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class BaseTaskManagerTest<T extends TaskManager> {
    protected T taskManager;

    protected abstract T createTaskManager() throws IOException;

    @BeforeEach
    void setUp() throws IOException {
        taskManager = createTaskManager();
    }

    @Test
    void addTask_shouldAddAndUpdateTaskAndRetrieveItCorrectly() throws InvalidManagerTaskException {
        Task task1 = new Task("Task 1", "Description of Task 1");
        final int taskId = taskManager.addTask(task1);
        boolean isId = (taskId > 0);

        assertTrue(isId, "Added task should have positive id");

        task1 = task1.copy(taskId);
        Optional<Task> oTask1 = taskManager.getTaskById(taskId);

        assertTrue(oTask1.isPresent(), "Saved task does not exist in InMemoryTaskManager.");

        Task savedTask1 = oTask1.get();

        assertEquals(task1, savedTask1, "Added task does not match saved task.");

        savedTask1.setName(savedTask1.getName() + " Updated");
        savedTask1.setDescription(savedTask1.getDescription() + " Updated");
        savedTask1.setStatus(Status.IN_PROGRESS);
        taskManager.updateTask(savedTask1);
        oTask1 = taskManager.getTaskById(taskId);

        assertTrue(oTask1.isPresent(), "Saved task does not exist in InMemoryTaskManager.");

        final Task savedTask2 = oTask1.get();

        assertAll("Setters should properly update Task fields",
                () -> assertEquals("Task 1 Updated", savedTask2.getName(), "The name of the task should be updated"),
                () -> assertEquals("Description of Task 1 Updated", savedTask2.getDescription(), "The description of the task should be updated"),
                () -> assertEquals(Status.IN_PROGRESS, savedTask2.getStatus(), "The status of the task should be updated to IN_PROGRESS")
        );

        final List<Task> tasks = taskManager.getTasks();
        final Task finalTask1 = task1;

        assertAll("Manager should have correct number of tasks after adding Task",
                () -> assertNotNull(tasks, "getTasks() should return empty list."),
                () -> assertEquals(1, tasks.size(), "The wrong number of tasks obtained by getTasks()."),
                () -> assertEquals(finalTask1, tasks.getFirst(), "Added Task does not match saved task obtained by getTasks().")
        );
    }

    @Test
    void addEpic_shouldAddEpicWithSubtaskAndRetrieveItCorrectly() throws InvalidManagerTaskException {
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
    void addSubtask_shouldAddSubtasksToEpicAndRetrieveItCorrectly() throws InvalidManagerTaskException {
        Epic epic = new Epic("Epic 1", "Description of Epic 1");
        final int epicId = taskManager.addEpic(epic);
        epic = epic.copy(epicId);
        final Optional<Epic> oEpic = taskManager.getEpicById(epicId);

        Subtask subtaskNew1 = new Subtask("Subtask 1", "Description of Subtask 1");
        final int subtaskId1 = taskManager.addSubtask(subtaskNew1, epic);
        final Subtask subtask1 = subtaskNew1.copy(subtaskId1);
        final Optional<Subtask> savedSubtask1 = taskManager.getSubtaskById(subtaskId1);

        Subtask subtaskNew2 = new Subtask("Subtask 2", "Description of Subtask 2");
        final int subtaskId2 = taskManager.addSubtask(subtaskNew2, epic);
        final Subtask subtask2 = subtaskNew2.copy(subtaskId2);
        final Epic finalEpic = epic;

        assertAll("Task manager should contain correct number of Epics",
                () -> assertTrue(oEpic.isPresent(), "Saved Epic does not exist in InMemoryTaskManager."),
                () -> assertEquals(finalEpic, oEpic.orElseThrow(() -> new AssertionError("Epic not found")), "Added Epic does not match saved epic.")
        );

        final Optional<Subtask> oSavedSubtask2 = taskManager.getSubtaskById(subtaskId2);
        Subtask savedSubtask2 = oSavedSubtask2.orElseThrow(() -> new AssertionError("Subtask not found"));

        assertAll("Task manager should contain correct number of Subtasks",
                () -> assertEquals(subtask1, savedSubtask1.orElseThrow(() -> new AssertionError("Subtask not found")), "Added Subtask 1 does not match saved Subtask."),
                () -> assertEquals(subtask2, savedSubtask2, "Added Subtask 2 does not match saved Subtask.")
        );

        final List<Epic> epics = taskManager.getEpics();
        final Epic finalEpic1 = epic;

        assertAll("Epics list should contain correct number",
                () -> assertNotNull(epics, "Empty list of epics obtained by getTasks()."),
                () -> assertEquals(1, epics.size(), "The wrong number of epics obtained by getEpics()."),
                () -> assertEquals(finalEpic1, epics.getFirst(), "Added Epic does not match saved task obtained by getEpics().")
        );

        final Optional<Epic> oEpic1 = taskManager.getEpicById(epicId);
        final Epic savedEpic1 = oEpic1.orElseThrow(() -> new AssertionError("Epic not found"));
        final List<Subtask> subtasks1 = savedEpic1.getSubtasksList();
        final Optional<Epic> oEpicBySubtask = taskManager.getEpicBySubtask(savedSubtask2);

        assertAll("Task manager state should contain correct number of tasks",
                () -> assertEquals(savedEpic1, oEpicBySubtask.orElseThrow(() -> new AssertionError("Epic not found")), "Epics retrieved from Task manager and from Subtask should be same"),
                () -> assertEquals(taskManager.getSubtasksByEpicId(epicId), subtasks1, "Subtasks retrieved by epic should match the subtasks list in the epic"),
                () -> assertEquals(2, subtasks1.size(), "Epic should contain 2 subtasks after adding two subtasks"),
                () -> assertTrue(subtasks1.contains(subtask1), "Epic's subtasks list should contain subtask1"),
                () -> assertTrue(subtasks1.contains(subtask2), "Epic's subtasks list should contain subtask2")
        );

        taskManager.deleteSubtaskById(subtaskId2);
        Optional<Epic> oEpic2 = taskManager.getEpicById(epicId);

        assertTrue(oEpic2.isPresent(), "Epic should still present after deleting one of its subtasks");

        final Epic savedEpic2 = oEpic2.get();
        final List<Subtask> subtasks2 = savedEpic2.getSubtasksList();

        assertAll("Epic subtasks list should contain correct number of subtasks",
                () -> assertEquals(1, subtasks2.size(), "Epic should contain 1 subtask after deleting one subtask"),
                () -> assertTrue(subtasks2.contains(subtask1), "Epic's subtasks list should still contain subtask1 after deleting subtask2")
        );
    }

    @Test
    void testEpicUpdatesItStatusBySubtaskStatus() throws InvalidManagerTaskException {
        Epic epic = new Epic("Test Epic", "Test Description");

        int epicId = taskManager.addEpic(epic);
        Optional<Epic> oEpic = taskManager.getEpicById(epicId);

        assertTrue(oEpic.isPresent(), "Epic should be present in the task manager after being added");

        Epic savedEpic = oEpic.get();

        assertEquals(Status.NEW, savedEpic.getStatus(), "Epic status should be NEW when no subtasks are present");

        Subtask subtask1 = new Subtask("Test Subtask 1", "Test Subtask Description 1");
        Subtask subtask2 = new Subtask("Test Subtask 2", "Test Subtask Description 2");
        int subtaskId1 = taskManager.addSubtask(subtask1, savedEpic);
        taskManager.addSubtask(subtask2, savedEpic);

        Optional<Subtask> oSubtask1 = taskManager.getSubtaskById(subtaskId1);

        assertTrue(oSubtask1.isPresent(), "Subtask 1 should be present in the task manager after being added");

        Subtask savedSubtask1 = oSubtask1.get();
        savedSubtask1.setStatus(Status.DONE);
        taskManager.updateSubtask(savedSubtask1);

        oEpic = taskManager.getEpicById(epicId);

        assertTrue(oEpic.isPresent(), "Epic should still be present in the task manager after updating subtask status");

        savedEpic = oEpic.get();

        assertEquals(Status.IN_PROGRESS, savedEpic.getStatus(), "Epic status should be IN_PROGRESS when one subtask is DONE and the other is NEW");
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
        Task task1 = new Task("Task 1", "Description of Task 1");
        Task task2 = new Task("Task 2", "Description of Task 2");
        taskManager.addTask(task1);
        int taskId2 = taskManager.addTask(task2);

        Epic epic1 = new Epic("Epic 1", "Description of Epic 1");
        Epic epic2 = new Epic("Epic 2", "Description of Epic 2");
        int epicId1 = taskManager.addEpic(epic1);
        int epicId2 = taskManager.addEpic(epic2);
        Optional<Epic> oEpic1 = taskManager.getEpicById(epicId1);

        assertTrue(oEpic1.isPresent(), "Expected epic with ID" + epicId1 + " to exist, but it was not found.");

        epic1 = oEpic1.get();
        Optional<Epic> oEpic2 = taskManager.getEpicById(epicId2);

        assertTrue(oEpic2.isPresent(), "Expected epic with ID " + epicId2 + " to exist, but it was not found.");

        epic2 = oEpic2.get();
        Subtask subtask1 = new Subtask("Subtask 1", "Description of Subtask 1");
        Subtask subtask2 = new Subtask("Subtask 2", "Description of Subtask 2");
        taskManager.addSubtask(subtask1, epic1);
        int subtaskId2 = taskManager.addSubtask(subtask2, epic2);

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
        Optional<Task> oTask1 = taskManager.getTaskById(taskId1);
        Optional<Task> oTask2 = taskManager.getTaskById(taskId2);

        assertAll("Added tasks should present in the task manager",
                () -> assertTrue(oTask1.isPresent(), "Task 1 should be present in the task manager"),
                () -> assertTrue(oTask2.isPresent(), "Task 2 should be present in the task manager")
        );

        task1 = oTask1.get();
        task2 = oTask2.get();

        Epic epic1 = new Epic("Epic 1", "Description of Epic 1");
        Epic epic2 = new Epic("Epic 2", "Description of Epic 2");
        int epicId1 = taskManager.addEpic(epic1);
        int epicId2 = taskManager.addEpic(epic2);

        Optional<Epic> oEpic1 = taskManager.getEpicById(epicId1);
        Optional<Epic> oEpic2 = taskManager.getEpicById(epicId2);

        assertAll("Added epics should present in the task manager",
                () -> assertTrue(oEpic1.isPresent(), "Epic 1 should be present in the task manager"),
                () -> assertTrue(oEpic2.isPresent(), "Epic 2 should be present in the task manager")
        );

        epic1 = oEpic1.get();
        epic2 = oEpic2.get();

        Subtask subtask1 = new Subtask("Subtask 1", "Description of Subtask 1");
        Subtask subtask2 = new Subtask("Subtask 2", "Description of Subtask 2");
        Subtask subtask3 = new Subtask("Subtask 3", "Description of Subtask 3");
        int subtaskId1 = taskManager.addSubtask(subtask1, epic1);
        int subtaskId2 = taskManager.addSubtask(subtask2, epic1);
        int subtaskId3 = taskManager.addSubtask(subtask3, epic1);
        Optional<Subtask> oSubtask1 = taskManager.getSubtaskById(subtaskId1);
        Optional<Subtask> oSubtask2 = taskManager.getSubtaskById(subtaskId2);
        Optional<Subtask> oSubtask3 = taskManager.getSubtaskById(subtaskId3);

        assertAll("Added subtasks should present in the task manager",
                () -> assertTrue(oSubtask1.isPresent(), "Subtask 1 should be present in the task manager"),
                () -> assertTrue(oSubtask2.isPresent(), "Subtask 2 should be present in the task manager"),
                () -> assertTrue(oSubtask3.isPresent(), "Subtask 3 should be present in the task manager")
        );

        subtask1 = oSubtask1.get();
        subtask2 = oSubtask2.get();
        subtask3 = oSubtask3.get();
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
        int epicId = taskManager.addEpic(epic);
        epic = epic.copy(epicId);

        Subtask subtask = new Subtask("Subtask 1", "Description of Subtask 1");
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
        Optional<Epic> oEpic = taskManager.getEpicById(epicId);
        final Epic epicSaved = oEpic.orElseThrow(() -> new AssertionError("Epic not found"));

        Subtask subtaskNew1 = new Subtask("Subtask 1", "Description of Subtask 1");
        Subtask subtaskNew2 = new Subtask("Subtask 2", "Description of Subtask 2");
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

}
