package ru.yandex.practicum.taskmanager.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.taskmanager.model.Epic;
import ru.yandex.practicum.taskmanager.model.Status;
import ru.yandex.practicum.taskmanager.model.Subtask;
import ru.yandex.practicum.taskmanager.model.Task;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryTaskManagerTest {

    private InMemoryTaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = InMemoryTaskManager.getInstance();
        taskManager.clearAll();
    }

    @Test
    void addTask_shouldAddAndUpdateTaskAndRetrieveItCorrectly() {
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
        savedTask1 = oTask1.get();

        assertEquals("Task 1 Updated", savedTask1.getName(), "The name of the task should be updated");
        assertEquals("Description of Task 1 Updated", savedTask1.getDescription(), "The description of the task should be updated");
        assertEquals(Status.IN_PROGRESS, savedTask1.getStatus(), "The status of the task should be updated to IN_PROGRESS");

        final List<Task> tasks = taskManager.getTasks();

        assertNotNull(tasks, "getTasks() should return empty list.");
        assertEquals(1, tasks.size(), "The wrong number of tasks obtained by getTasks().");
        assertEquals(task1, tasks.getFirst(), "Added Task does not match saved task obtained by getTasks().");
    }

    @Test
    void addEpic_shouldAddEpicWithSubtaskAndRetrieveItCorrectly() {
        Epic epic = new Epic("Epic 1", "Description of Epic 1");
        final int epicId = taskManager.addEpic(epic);
        epic = epic.copy(epicId);
        final Optional<Epic> oEpic = taskManager.getEpicById(epicId);

        assertTrue(oEpic.isPresent(), "Saved Epic does not exist in InMemoryTaskManager.");
        assertEquals(epic, oEpic.get(), "Added Epic does not match saved Epic.");

        final List<Epic> epics = taskManager.getEpics();

        assertNotNull(epics, "Empty list of Epics obtained by getTasks().");
        assertEquals(1, epics.size(), "The wrong number of Epics obtained by getEpics().");
        assertEquals(epic, epics.getFirst(), "Added Epic does not match saved task obtained by getEpics().");
    }

    @Test
    void addSubtask_shouldAddSubtasksToEpicAndRetrieveItCorrectly() {
        Epic epic = new Epic("Epic 1", "Description of Epic 1");
        final int epicId = taskManager.addEpic(epic);
        epic = epic.copy(epicId);
        final Optional<Epic> oEpic = taskManager.getEpicById(epicId);

        Subtask subtask1 = new Subtask("Subtask 1", "Description of Subtask 1");
        final int subtaskId1 = taskManager.addSubtask(subtask1, epic);
        subtask1 = subtask1.copy(subtaskId1);
        final Optional<Subtask> savedSubtask1 = taskManager.getSubtaskById(subtaskId1);

        Subtask subtask2 = new Subtask("Subtask 2", "Description of Subtask 2");
        final int subtaskId2 = taskManager.addSubtask(subtask2, epic);
        subtask2 = subtask2.copy(subtaskId2);
        final Optional<Subtask> oSavedSubtask2 = taskManager.getSubtaskById(subtaskId2);
        assertTrue(oSavedSubtask2.isPresent(), "Subtask " + subtaskId2 + " should be present in the task manager");
        Subtask savedSubtask2 = oSavedSubtask2.get();

        assertTrue(oEpic.isPresent(), "Saved Epic does not exist in InMemoryTaskManager.");
        assertEquals(epic, oEpic.get(), "Added Epic does not match saved epic.");
        assertTrue(savedSubtask1.isPresent(), "Saved Subtask 1 does not exist in InMemoryTaskManager.");
        assertEquals(subtask1, savedSubtask1.get(), "Added Subtask 1 does not match saved Subtask.");
        assertEquals(subtask2, savedSubtask2, "Added Subtask 2 does not match saved Subtask.");

        final List<Epic> epics = taskManager.getEpics();

        assertNotNull(epics, "Empty list of epics obtained by getTasks().");
        assertEquals(1, epics.size(), "The wrong number of epics obtained by getEpics().");
        assertEquals(epic, epics.getFirst(), "Added Epic does not match saved task obtained by getEpics().");

        Optional<Epic> oEpic1 = taskManager.getEpicById(epicId);

        assertTrue(oEpic1.isPresent(), "Epic " + epicId + " should be present in the task manager");

        Epic savedEpic1 = oEpic1.get();

        List<Subtask> subtasks = savedEpic1.getSubtasksList();
        Optional<Epic> oEpicBySubtask = taskManager.getEpicBySubtask(savedSubtask2);

        assertTrue(oEpicBySubtask.isPresent(), "Epic should be present by subtask");
        assertEquals(savedEpic1, oEpicBySubtask.get(), "Epics retrieved from Task manager and from Subtask should be same");
        assertEquals(taskManager.getSubtasksByEpic(savedEpic1), subtasks, "Subtasks retrieved by epic should match the subtasks list in the epic");
        assertEquals(2, subtasks.size(), "Epic should contain 2 subtasks after adding two subtasks");
        assertTrue(subtasks.contains(subtask1), "Epic's subtasks list should contain subtask1");
        assertTrue(subtasks.contains(subtask2), "Epic's subtasks list should contain subtask2");

        taskManager.deleteSubtaskById(subtaskId2);
        oEpic1 = taskManager.getEpicById(epicId);

        assertTrue(oEpic1.isPresent(), "Epic should still be present after deleting one of its subtasks");

        savedEpic1 = oEpic1.get();

        subtasks = savedEpic1.getSubtasksList();
        assertEquals(1, subtasks.size(), "Epic should contain 1 subtask after deleting one subtask");
        assertTrue(subtasks.contains(subtask1), "Epic's subtasks list should still contain subtask1 after deleting subtask2");
    }

    @Test
    void getInstance_shouldReturnSameInstance() {
        TaskManager instance1 = InMemoryTaskManager.getInstance();
        TaskManager instance2 = InMemoryTaskManager.getInstance();

        assertSame(instance1, instance2, "getInstance() should return the same instance of InMemoryTaskManager.");
    }

    @Test
    void testEpicUpdatesItStatusBySubtaskStatus() {
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
        assertTrue(taskManager.getTaskById(123).isEmpty(),
                "Expected no task to be found with ID 123, but a task was returned.");
        assertTrue(taskManager.getSubtaskById(456).isEmpty(),
                "Expected no subtask to be found with ID 456, but a subtask was returned.");
        assertTrue(taskManager.getEpicById(789).isEmpty(),
                "Expected no epic to be found with ID 789, but a epic was returned.");
    }

    @Test
    void testDeleteTypeOfTaskShouldWorkCorrectly() {
        Task task1 = new Task("Task 1", "Description of Task 1");
        Task task2 = new Task("Task 2", "Description of Task 2");
        taskManager.addTask(task1);
        int taskId2 = taskManager.addTask(task2);
        List<Task> taskList = taskManager.getTasks();

        Epic epic1 = new Epic("Epic 1", "Description of Epic 1");
        Epic epic2 = new Epic("Epic 2", "Description of Epic 2");
        int epicId1 = taskManager.addEpic(epic1);
        int epicId2 = taskManager.addEpic(epic2);
        List<Epic> epicList = taskManager.getEpics();
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
        List<Subtask> subtaskList = taskManager.getSubtasks();

        assertEquals(2, taskList.size(), "Expected 2 tasks, but found " + taskList.size() + " tasks.");
        assertEquals(2, subtaskList.size(), "Expected 2 subtasks, but found " + subtaskList.size() + " subtasks.");
        assertEquals(2, epicList.size(), "Expected 2 epics, but found " + epicList.size() + " epics.");

        taskManager.deleteTaskById(taskId2);
        taskManager.deleteSubtaskById(subtaskId2);
        taskManager.deleteEpicById(epicId2);
        taskList = taskManager.getTasks();
        subtaskList = taskManager.getSubtasks();
        epicList = taskManager.getEpics();

        assertEquals(1, taskList.size(), "Expected 1 task after deletion, but found " + taskList.size() + " tasks.");
        assertEquals(1, subtaskList.size(), "Expected 1 subtask after deletion, but found " + subtaskList.size() + " subtasks.");
        assertEquals(1, epicList.size(), "Expected 1 epic after deletion, but found " + epicList.size() + " epics.");

        taskManager.deleteTasks();
        taskManager.deleteSubtasks();
        taskManager.deleteEpics();
        taskList = taskManager.getTasks();
        subtaskList = taskManager.getSubtasks();
        epicList = taskManager.getEpics();

        assertTrue(taskList.isEmpty(), "Expected no tasks to be deleted, but found " + taskList.size() + " tasks.");
        assertTrue(subtaskList.isEmpty(), "Expected subtask list to be empty after deletion, but it contains " + subtaskList.size() + " subtasks.");
        assertTrue(epicList.isEmpty(), "Expected epic list to be empty after deletion, but it contains " + epicList.size() + " epics.");
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
    void testUserScenarioSprint6ShouldWorkCorrectly() {
        Task task1 = new Task("Task 1", "Description of Task 1");
        Task task2 = new Task("Task 2", "Description of Task 2");
        int taskId1 = taskManager.addTask(task1);
        int taskId2 = taskManager.addTask(task2);
        Optional<Task> oTask1 = taskManager.getTaskById(taskId1);

        assertTrue(oTask1.isPresent(), "Task 1 should be present in the task manager");

        task1 = oTask1.get();
        Optional<Task> oTask2 = taskManager.getTaskById(taskId2);

        assertTrue(oTask2.isPresent(), "Task 2 should be present in the task manager");

        task2 = oTask2.get();

        Epic epic1 = new Epic("Epic 1", "Description of Epic 1");
        Epic epic2 = new Epic("Epic 2", "Description of Epic 2");
        int epicId1 = taskManager.addEpic(epic1);
        int epicId2 = taskManager.addEpic(epic2);

        Optional<Epic> oEpic1 = taskManager.getEpicById(epicId1);

        assertTrue(oEpic1.isPresent(), "Epic 1 should be present in the task manager");

        epic1 = oEpic1.get();
        Optional<Epic> oEpic2 = taskManager.getEpicById(epicId2);

        assertTrue(oEpic2.isPresent(), "Epic 2 should be present in the task manager");

        epic2 = oEpic2.get();

        Subtask subtask1 = new Subtask("Subtask 1", "Description of Subtask 1");
        Subtask subtask2 = new Subtask("Subtask 2", "Description of Subtask 2");
        Subtask subtask3 = new Subtask("Subtask 3", "Description of Subtask 3");
        int subtaskId1 = taskManager.addSubtask(subtask1, epic1);
        int subtaskId2 = taskManager.addSubtask(subtask2, epic1);
        int subtaskId3 = taskManager.addSubtask(subtask3, epic1);
        Optional<Subtask> oSubtask1 = taskManager.getSubtaskById(subtaskId1);

        assertTrue(oSubtask1.isPresent(), "Subtask 1 should be present in the task manager");

        subtask1 = oSubtask1.get();
        Optional<Subtask> oSubtask2 = taskManager.getSubtaskById(subtaskId2);

        assertTrue(oSubtask2.isPresent(), "Subtask 2 should be present in the task manager");

        subtask2 = oSubtask2.get();
        Optional<Subtask> oSubtask3 = taskManager.getSubtaskById(subtaskId3);

        assertTrue(oSubtask3.isPresent(), "Subtask 3 should be present in the task manager");

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

        List<Task> historyList = taskManager.getHistory();

        assertEquals(7, historyList.size(), "History list should contain 7 elements");
        assertTrue(historyList.containsAll(List.of(task1, task2, epic1, epic2, subtask1, subtask2, subtask3)), "History list should contain all viewed tasks, epics, and subtasks\"");

        taskManager.deleteTaskById(taskId2);
        historyList = taskManager.getHistory();

        assertEquals(6, historyList.size(), "History list should contain 7 elements");
        assertFalse(historyList.contains(task2), "History list should not contain Task 2 after deletion");

        taskManager.deleteEpicById(epicId1);
        historyList = taskManager.getHistory();
        boolean containsNoSubtasks = historyList.stream().noneMatch(element -> element instanceof Subtask);

        assertTrue(containsNoSubtasks, "History list should not contain any subtasks after deleting Epic 1");
        assertEquals(2, historyList.size(), "History list should contain 2 elements after deleting Epic 1 and its subtasks");
        assertTrue(historyList.contains(task1), "History list should still contain Task 1");
        assertTrue(historyList.contains(epic2), "History list should still contain Epic 2");
    }

}