package ru.yandex.practicum.taskmanager.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EpicTest {

    private Epic epic;
    private Subtask subtask1;
    private Subtask subtask2;

    @BeforeEach
    void setUp() {
        epic = new Epic("Test Epic", "Test Epic Description");
        subtask1 = new Subtask("Test Subtask 1", "Test Subtask Description 1");
        subtask2 = new Subtask("Test Subtask 2", "Test Subtask Description 2");
    }

    @Test
    void equals_shouldReturnTrueForEpicsWithSameId() {
        epic.setId(1);
        epic.addSubtask(subtask1);

        Epic epic2 = new Epic("Test Epic 2", "Test Epic Description 2");
        epic2.setId(1);
        epic.addSubtask(subtask2);

        Epic epic3 = new Epic("Test Epic", "Test Epic Description");
        epic3.setId(2);
        epic3.addSubtask(subtask1);

        assertEquals(epic, epic2, "Epics with same id should be equals");
        assertNotEquals(epic, epic3, "Epics with different id and same fields should not be equals");
    }

    @Test
    void constructor_shouldReturnProperlyInitializedEpicObject() {
        assertNotNull(epic);
        assertEquals("Test Epic", epic.getName());
        assertEquals("Test Epic Description", epic.getDescription());
        assertEquals(Status.NEW, epic.getStatus());
        assertEquals(0, epic.getId());
        assertTrue(epic.getSubtasksList().isEmpty());
    }

    @Test
    void copy_ShouldReturnSameFieldsOfEpicObject() {
        epic.setId(1);
        epic.setStatus(Status.IN_PROGRESS);
        epic.addSubtask(subtask1);
        epic.addSubtask(subtask2);

        Epic copiedEpic = epic.copy();

        assertEquals(epic.getId(), copiedEpic.getId());
        assertEquals(epic.getName(), copiedEpic.getName());
        assertEquals(epic.getDescription(), copiedEpic.getDescription());
        assertEquals(epic.getStatus(), copiedEpic.getStatus());
        assertEquals(epic.getSubtasksList().size(), copiedEpic.getSubtasksList().size());
    }

    @Test
    void addAndRemoveFromSubtasksListShouldWorkCorrectly() {
        subtask1.setId(1);
        subtask2.setId(2);

        epic.addSubtask(subtask1);
        epic.addSubtask(subtask2);

        List<Subtask> subtasks = epic.getSubtasksList();

        assertEquals(2, subtasks.size());
        assertTrue(subtasks.contains(subtask1));
        assertTrue(subtasks.contains(subtask2));

        epic.removeSubtask(subtask1);

        subtasks = epic.getSubtasksList();

        assertEquals(1, subtasks.size());
        assertFalse(subtasks.contains(subtask1));
        assertTrue(subtasks.contains(subtask2));

        epic.removeSubtask(subtask2);
        subtasks = epic.getSubtasksList();
        assertTrue(subtasks.isEmpty());
    }

    @Test
    void getSubtasksList_ShouldReturnUnmodifiableList() {
        subtask1.setId(1);
        epic.addSubtask(subtask1);

        List<Subtask> subtasks = epic.getSubtasksList();

        assertThrows(UnsupportedOperationException.class, () -> subtasks.add(subtask2));
    }

}