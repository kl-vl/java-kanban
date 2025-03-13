package ru.yandex.practicum.taskmanager.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.taskmanager.model.Status;
import ru.yandex.practicum.taskmanager.model.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryHistoryManagerTest {

    private InMemoryHistoryManager historyManager;

    private Task task1;

    @BeforeEach
    void setUp() {
        historyManager = InMemoryHistoryManager.getInstance();
        // needs to be cleared before each test as InMemoryHistoryManager use Singleton
        historyManager.clearHistory();
        task1 = new Task("Task 1", "Description of Task 1");
        task1 = task1.copy(1);
    }

    @Test
    void add_shouldAddTaskToHistory() {
        historyManager.add(task1);
        final List<Task> tasks = historyManager.getHistory();

        assertEquals(1, tasks.size(), "History should contain only 1 task.");
        assertEquals(task1, tasks.getFirst(), "Added Task 1 should exist in History.");
    }

    @Test
    void testTaskHistoryPreservesPreviousVersionOfTasks() {
        historyManager.add(task1);

        task1.setName(task1.getName() + " Updated");
        task1.setDescription(task1.getDescription() + " Updated");
        task1.setStatus(Status.IN_PROGRESS);

        List<Task> history = historyManager.getHistory();

        assertEquals(1, history.size(), "History should contain one task.");

        Task savedTask = history.getFirst();

        assertNotSame(task1, savedTask, "The task in history should be in state of viewed object, not the updated task.");
        assertEquals("Task 1", savedTask.getName(), "The name of the task in history should not be updated.");
        assertEquals("Description of Task 1", savedTask.getDescription(), "The description of the task in history should not be updated.");
        assertEquals(Status.NEW, savedTask.getStatus(), "The status of the task in history should not be updated.");

        historyManager.add(task1);
        history = historyManager.getHistory();

        assertEquals(1, history.size(), "History should contain one last task.");

        Task savedTask2 = history.getFirst();

        assertNotSame(task1, savedTask2, "The task in history should be a copy, not the same object.");
        assertEquals("Task 1 Updated", savedTask2.getName(), "The name of the task in history should not be updated.");
        assertEquals("Description of Task 1 Updated", savedTask2.getDescription(), "The description of the task in history should not be updated.");
        assertEquals(Status.IN_PROGRESS, savedTask2.getStatus(), "The status of the task in history should not be updated.");
    }

    @Test
    void getInstance_shouldReturnSameInstance() {
        HistoryManager instance1 = InMemoryHistoryManager.getInstance();
        HistoryManager instance2 = InMemoryHistoryManager.getInstance();

        assertSame(instance1, instance2, "getInstance() should return the same instance of InMemoryHistoryManager.");
    }

    @Test
    void clear_shouldRemoveAllTasksFromHistory() {
        Task task2 = new Task("Task 2", "Description of Task 2");
        task2 = task2.copy(2);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.clearHistory();
        final List<Task> history = historyManager.getHistory();

        assertTrue(history.isEmpty(), "History should be empty after clearHistory() call.");
    }

}