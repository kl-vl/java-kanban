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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryTaskManagerTest {

    private InMemoryTaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = InMemoryTaskManager.getInstance();
        taskManager.deleteTasks();
        taskManager.deleteSubtasks();
        taskManager.deleteEpics();
    }

    @Test
    void addTask_shouldAddTaskAndRetrieveItCorrectly() {
        Task task1 = new Task("Task 1", "Description of Task 1");
        final int taskId = taskManager.addTask(task1);
        boolean isId = (taskId > 0);
        assertTrue(isId,"Added task should have positive id");
        task1.setId(taskId);
        final Optional<Task> savedTask = taskManager.getTaskById(taskId);

        assertTrue(savedTask.isPresent(), "Saved task does not exist in InMemoryTaskManager.");
        assertEquals(task1, savedTask.get(), "Added task does not match saved task.");

        final List<Task> tasks = taskManager.getTasks();

        assertNotNull(tasks, "getTasks() returns empty list.");
        assertEquals(1, tasks.size(), "The wrong number of tasks obtained by getTasks().");
        assertEquals(task1, tasks.getFirst(), "Added task does not match saved task obtained by getTasks().");
    }

    @Test
    void addEpic_shouldAddEpicWithSubtaskAndRetrieveItCorrectly() {
        Epic epic = new Epic("Epic 1", "Description of Epic 1");
        final int epicId = taskManager.addEpic(epic);
        epic.setId(epicId);
        final Optional<Epic> savedEpic  = taskManager.getEpicById(epicId);

        assertTrue(savedEpic.isPresent(), "Saved epic does not exist in InMemoryTaskManager.");
        assertEquals(epic, savedEpic.get(), "Added epic does not match saved epic.");

        final List<Epic> epics = taskManager.getEpics();

        assertNotNull(epics, "Empty list of epics obtained by getTasks().");
        assertEquals(1, epics.size(), "The wrong number of epics obtained by getEpics().");
        assertEquals(epic, epics.getFirst(), "Added epic does not match saved task obtained by getEpics().");
    }


    @Test
    void addSubtask_shouldAddSubtasksToEpicAndRetrieveItCorrectly() {
        Epic epic = new Epic("Epic 1", "Description of Epic 1");
        final int epicId = taskManager.addEpic(epic);
        epic.setId(epicId);
        final Optional<Epic> savedEpic  = taskManager.getEpicById(epicId);

        Subtask subtask1 = new Subtask("Subtask 1", "Description of Subtask 1");
        final int subtaskId1 = taskManager.addSubtask(subtask1, epic);
        subtask1.setId(subtaskId1);
        final Optional<Subtask> savedSubtask1  = taskManager.getSubtaskById(subtaskId1);

        Subtask subtask2 = new Subtask("Subtask 2", "Description of Subtask 2");
        final int subtaskId2 = taskManager.addSubtask(subtask2, epic);
        subtask2.setId(subtaskId2);
        final Optional<Subtask> savedSubtask2  = taskManager.getSubtaskById(subtaskId2);


        assertTrue(savedEpic.isPresent(), "Saved Epic does not exist in InMemoryTaskManager.");
        assertEquals(epic, savedEpic.get(), "Added Epic does not match saved epic.");
        assertTrue(savedSubtask1.isPresent(), "Saved Subtask 1 does not exist in InMemoryTaskManager.");
        assertEquals(subtask1, savedSubtask1.get(), "Added Subtask 1 does not match saved Subtask.");
        assertTrue(savedSubtask2.isPresent(), "Saved Subtask 2 does not exist in InMemoryTaskManager.");
        assertEquals(subtask2, savedSubtask2.get(), "Added Subtask 2 does not match saved Subtask.");

        final List<Epic> epics = taskManager.getEpics();

        assertNotNull(epics, "Empty list of epics obtained by getTasks().");
        assertEquals(1, epics.size(), "The wrong number of epics obtained by getEpics().");
        assertEquals(epic, epics.getFirst(), "Added Epic does not match saved task obtained by getEpics().");

        Optional<Epic> oRetrievedEpic = taskManager.getEpicById(epicId);
        assertTrue(oRetrievedEpic.isPresent());
        Epic retrievedEpic = oRetrievedEpic.get();

        List<Subtask> subtasks = retrievedEpic.getSubtasksList();
        assertEquals(2, subtasks.size());
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
        Optional<Epic> oRetrievedEpic = taskManager.getEpicById(epicId);
        assertTrue(oRetrievedEpic.isPresent());
        Epic retrievedEpic = oRetrievedEpic.get();
        assertEquals(Status.NEW, retrievedEpic.getStatus());

        Subtask subtask1 = new Subtask("Test Subtask 1", "Test Subtask Description 1");
        Subtask subtask2 = new Subtask("Test Subtask 2", "Test Subtask Description 2");
        int subtaskId1 = taskManager.addSubtask(subtask1, retrievedEpic);
        taskManager.addSubtask(subtask2, retrievedEpic);

        Optional<Subtask> oRetrievedSubtask1 = taskManager.getSubtaskById(subtaskId1);
        assertTrue(oRetrievedSubtask1.isPresent());

        Subtask retrievedSubtask1= oRetrievedSubtask1.get();
        retrievedSubtask1.setStatus(Status.DONE);
        taskManager.updateSubtask(retrievedSubtask1);

        oRetrievedEpic = taskManager.getEpicById(epicId);
        assertTrue(oRetrievedEpic.isPresent());
        retrievedEpic = oRetrievedEpic.get();
        assertEquals(Status.IN_PROGRESS, retrievedEpic.getStatus());
    }

}