package ru.yandex.practicum.taskmanager.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.taskmanager.model.Status;
import ru.yandex.practicum.taskmanager.model.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryHistoryManagerTest {

    private InMemoryHistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = InMemoryHistoryManager.getInstance();
    }

    @Test
    void add_shouldAddTaskToHistory() {
        Task task1 = new Task("Task 1", "Description of Task 1");
        task1.setId(1);

        historyManager.add(task1);
        final List<Task> tasks = historyManager.getHistory();

        assertEquals(1, tasks.size(), "History should contain only 1 task.");
        assertEquals(task1, tasks.getFirst(), "Added Task 1 should exist in History.");
    }

    @Test
    void testTaskHistoryPreservesPreviousVersionOfTasks() {
        Task task = new Task("Task 1", "Task Description 1");
        task.setId(1);

        historyManager.add(task);

        task.setName("Updated Task 1");
        task.setDescription("Updated Task Description 1");
        task.setStatus(Status.IN_PROGRESS);

        List<Task> history = historyManager.getHistory();

        assertEquals(1, history.size(), "History should contain one task.");

        Task savedTask = history.getFirst();

        assertNotSame(task, savedTask, "The task in history should be a copy, not the same object.");
        assertEquals("Task 1", savedTask.getName(), "The name of the task in history should not be updated.");
        assertEquals("Task Description 1", savedTask.getDescription(), "The description of the task in history should not be updated.");
        assertEquals(Status.NEW, savedTask.getStatus(), "The status of the task in history should not be updated.");

        historyManager.add(task);
        history = historyManager.getHistory();

        assertEquals(2, history.size(), "History should contain two tasks.");

        Task savedTask2 = history.get(1);

        assertNotSame(task, savedTask2, "The task in history should be a copy, not the same object.");
        assertEquals("Updated Task 1", savedTask2.getName(), "The name of the task in history should not be updated.");
        assertEquals("Updated Task Description 1", savedTask2.getDescription(), "The description of the task in history should not be updated.");
        assertEquals(Status.IN_PROGRESS, savedTask2.getStatus(), "The status of the task in history should not be updated.");
    }

    @Test
    void getInstance_shouldReturnSameInstance() {
        HistoryManager instance1 = InMemoryHistoryManager.getInstance();
        HistoryManager instance2 = InMemoryHistoryManager.getInstance();

        assertSame(instance1, instance2, "getInstance() should return the same instance of InMemoryHistoryManager.");
    }

    @Test
    void add_shouldLimitHistorySize() {
        for (int i = 1; i <= 11; i++) {
            Task task = new Task("Task " + i, "Description of Task " + i);
            task.setId(i);
            historyManager.add(task);
        }

        final List<Task> history = historyManager.getHistory();

        assertEquals(10, history.size(), "History should not exceed MAX_HISTORY_SIZE.");
        assertEquals("Task 11", history.getLast().getName(), "The last added task should be the last in history.");
        assertEquals("Task 2", history.getFirst().getName(), "The oldest task should be Task 2.");
    }

    @Test
    void getHistory_shouldReturnUnmodifiableList() {
        Task task1 = new Task("Task 1", "Description of Task 1");
        task1.setId(1);
        Task task2 = new Task("Task 2", "Description of Task 2");
        task2.setId(2);

        historyManager.add(task1);
        final List<Task> history = historyManager.getHistory();

        assertThrows(UnsupportedOperationException.class, () -> history.add(task2), "getHistory() should return the unchanged list.");
    }

    @Test
    void clear_shouldRemoveAllTasksFromHistory() {
        Task task1 = new Task("Task 1", "Description of Task 1");
        task1.setId(1);
        Task task2 = new Task("Task 2", "Description of Task 2");
        task2.setId(2);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.clearHistory();
        final List<Task> history = historyManager.getHistory();

        assertTrue(history.isEmpty(), "History should be empty after clearHistory() call.");
    }

}