package ru.yandex.practicum.taskmanager.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TaskTest {

    private Task task;

    @BeforeEach
    void setUp() {
        task = new Task("Test Task", "Test Task Description");
    }

    @Test
    void equals_shouldReturnTrueForTasksWithSameId() {
        task.setId(1);

        Task task2 = new Task("Test Task 2", "Test Task Description 2");
        task2.setId(1);

        Task task3 = task.copy();
        task3.setId(2);

        assertEquals(task, task2,"Tasks with same id should be equals");
        assertNotEquals(task, task3,"Tasks with different id and same fields should not be equals");
    }

    @Test
    void constructor_shouldReturnProperlyInitializedTaskObject() {
        assertNotNull(task);
        assertEquals("Test Task", task.getName());
        assertEquals("Test Task Description", task.getDescription());
        assertEquals(Status.NEW, task.getStatus());
        assertEquals(0, task.getId());
    }

    @Test
    void copy_ShouldReturnSameFieldsOfTaskObject() {
        task.setId(1);
        task.setStatus(Status.IN_PROGRESS);

        Task copiedTask = task.copy();

        assertEquals(task.getId(), copiedTask.getId());
        assertEquals(task.getName(), copiedTask.getName());
        assertEquals(task.getDescription(), copiedTask.getDescription());
        assertEquals(task.getStatus(), copiedTask.getStatus());
    }

    @Test
    void settersAndGettersShouldWork() {
        task.setId(1);
        task.setName("Updated Task");
        task.setDescription("Updated Description");
        task.setStatus(Status.DONE);

        assertEquals(1, task.getId());
        assertEquals("Updated Task", task.getName());
        assertEquals("Updated Description", task.getDescription());
        assertEquals(Status.DONE, task.getStatus());
    }
}