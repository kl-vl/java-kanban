package ru.yandex.practicum.taskmanager.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class SubtaskTest {

    private Subtask subtask;
    private Epic epic;

    @BeforeEach
    void setUp() {
        subtask = new Subtask("Test Subtask", "Test Subtask Description");
        epic = new Epic("Test Epic", "Test Epic Description");
    }

    @Test
    void equals_shouldReturnTrueForSubtasksWithSameId() {
        subtask.setId(1);
        subtask.setEpic(epic);

        Subtask subtask2 = new Subtask("Test Subtask 2", "Test Subtask Description 2");
        subtask2.setStatus(Status.IN_PROGRESS);
        subtask2.setId(1);

        Subtask subtask3 = subtask.copy();
        subtask3.setId(2);

        assertEquals(subtask, subtask2, "Subtasks with same id should be equals");
        assertNotEquals(subtask, subtask3, "Subtasks with different id and same fields should not be equals");
    }

    @Test
    void constructor_shouldReturnProperlyInitializedSubtaskObject() {
        assertNotNull(subtask);
        assertEquals("Test Subtask", subtask.getName());
        assertEquals("Test Subtask Description", subtask.getDescription());
        assertEquals(Status.NEW, subtask.getStatus());
        assertEquals(0, subtask.getId());
        assertNull(subtask.getEpic());
    }

    @Test
    void copy_ShouldReturnSameFieldsOfSubtaskObject() {
        subtask.setId(1);
        subtask.setStatus(Status.IN_PROGRESS);
        subtask.setEpic(epic);

        Subtask copiedSubtask = subtask.copy();

        assertEquals(subtask.getId(), copiedSubtask.getId());
        assertEquals(subtask.getName(), copiedSubtask.getName());
        assertEquals(subtask.getDescription(), copiedSubtask.getDescription());
        assertEquals(subtask.getStatus(), copiedSubtask.getStatus());
        assertEquals(subtask.getEpic(), copiedSubtask.getEpic());
    }
    @Test
    void assignEpicToSubtaskShouldWorkCorrectly() {
        epic.setId(1);
        subtask.setEpic(epic);
        assertEquals(epic, subtask.getEpic());

        Epic newEpic = new Epic("New Epic", "Test Epic Description");
        newEpic.setId(2);
        subtask.setEpic(newEpic);
        assertEquals(newEpic, subtask.getEpic());
    }

}