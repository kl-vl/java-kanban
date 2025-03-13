package ru.yandex.practicum.taskmanager.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EpicTest {

    private Epic epic1;
    private Subtask subtask1;
    private Subtask subtask2;

    @BeforeEach
    void setUp() {
        epic1 = new Epic("Test Epic 1", "Test Epic 1 Description");
        epic1 = epic1.copy(1);
        subtask1 = new Subtask("Test Subtask 1", "Test Subtask Description 1");
        subtask2 = new Subtask("Test Subtask 2", "Test Subtask Description 2");
    }

    @Test
    void equals_shouldReturnTrueForEpicsWithSameId() {
        epic1.addSubtasksList(subtask1);

        Epic epic2 = new Epic("Test Epic 2", "Test Epic Description 2");
        epic2 = epic2.copy(1);
        epic1.addSubtasksList(subtask2);

        Epic epic3 = new Epic("Test Epic", "Test Epic Description");
        epic3 = epic3.copy(2);
        epic3.addSubtasksList(subtask1);

        assertEquals(epic1, epic2, "Epics with same id should be equals");
        assertNotEquals(epic1, epic3, "Epics with different id and same fields should not be equals");
    }

    @Test
    void constructor_shouldReturnProperlyInitializedEpicObject() {
        Epic epic2 = new Epic("Test Epic 2", "Test Epic 2 Description");

        assertNotNull(epic2);
        assertEquals("Test Epic 2", epic2.getName());
        assertEquals("Test Epic 2 Description", epic2.getDescription());
        assertEquals(Status.NEW, epic2.getStatus());
        assertEquals(0, epic2.getId());
        assertTrue(epic2.getSubtasksList().isEmpty());
    }

    @Test
    void copy_ShouldReturnSameFieldsOfEpicObject() {
        epic1.setStatus(Status.IN_PROGRESS);
        epic1.addSubtasksList(subtask1);
        epic1.addSubtasksList(subtask2);

        Epic copiedEpic = epic1.copy();

        assertEquals(epic1.getId(), copiedEpic.getId());
        assertEquals(epic1.getName(), copiedEpic.getName());
        assertEquals(epic1.getDescription(), copiedEpic.getDescription());
        assertEquals(epic1.getStatus(), copiedEpic.getStatus());
        assertEquals(epic1.getSubtasksList().size(), copiedEpic.getSubtasksList().size());
    }

    @Test
    void addAndRemoveFromSubtasksListShouldWorkCorrectly() {
        subtask1 = subtask1.copy(2);
        subtask2 = subtask2.copy(3);

        epic1.addSubtasksList(subtask1);
        epic1.addSubtasksList(subtask2);

        List<Subtask> subtasks = epic1.getSubtasksList();

        assertEquals(2, subtasks.size());
        assertTrue(subtasks.contains(subtask1));
        assertTrue(subtasks.contains(subtask2));

        epic1.removeSubtask(subtask1);

        subtasks = epic1.getSubtasksList();

        assertEquals(1, subtasks.size());
        assertFalse(subtasks.contains(subtask1));
        assertTrue(subtasks.contains(subtask2));

        epic1.removeSubtask(subtask2);
        subtasks = epic1.getSubtasksList();
        assertTrue(subtasks.isEmpty());

        PrintStream originalOut = System.out;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(byteArrayOutputStream);
        System.setOut(out);

        epic1.addSubtasksList(null);
        System.setOut(originalOut);

        assertEquals("Subtask cannot be null." + System.lineSeparator(), byteArrayOutputStream.toString());
    }

    @Test
    void getSubtasksList_ShouldReturnUnmodifiableList() {
        subtask1 = subtask1.copy(1);
        epic1.addSubtasksList(subtask1);

        List<Subtask> subtasks = epic1.getSubtasksList();

        assertThrows(UnsupportedOperationException.class, () -> subtasks.add(subtask2));
    }


    @Test
    void hashCode_shouldWorkCorrectly() {
        Epic epic2 = new Epic("Test Epic 2", "Test Epic 2 Description");
        epic2 = epic2.copy(1);

        Epic epic3 = new Epic("Test Epic 3", "Test Epic 3 Description");
        epic3 = epic3.copy(2);

        assertEquals(epic1.hashCode(), epic2.hashCode(), "Epic with same id should return same hash code value");
        assertNotEquals(epic1.hashCode(), epic3.hashCode(), "Epic with different id should return different hash code values");
    }


    @Test
    void toString_ShouldWorkCorrectly() {
        epic1.setStatus(Status.NEW);
        subtask1 = subtask1.copy(2);
        epic1.addSubtasksList(subtask1);

        String expected = "Epic{id=1, name='Test Epic 1', description='Test Epic 1 Description', status='NEW', subtasksList=[2]}";
        assertEquals(expected, epic1.toString());
    }

}