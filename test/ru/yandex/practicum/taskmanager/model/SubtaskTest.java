package ru.yandex.practicum.taskmanager.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class SubtaskTest {

    private Subtask subtask1;
    private Epic epic1;

    @BeforeEach
    void setUp() {
        subtask1 = new Subtask("Test Subtask 1", "Test Subtask 1 Description");
        subtask1 = subtask1.copy(1);
        epic1 = new Epic("Test Epic 1", "Test Epic 1 Description");
    }

    @Test
    void equals_shouldReturnTrueForSubtasksWithSameId() {
        subtask1.setEpic(epic1);

        Subtask subtask2 = new Subtask("Test Subtask 2", "Test Subtask 2 Description");
        subtask2.setStatus(Status.IN_PROGRESS);
        subtask2 = subtask2.copy(1);
        Subtask subtask3 = subtask1.copy(2);

        assertEquals(subtask1, subtask2, "Subtasks with same id should be equals");
        assertNotEquals(subtask1, subtask3, "Subtasks with different id and same fields should not be equals");
        assertNotEquals(subtask1, subtask3, "Tasks with different id and same fields should not be equals");
        assertNotEquals(null, subtask1);
        assertNotEquals(new Object(), subtask1);
    }

    @Test
    void constructor_shouldReturnProperlyInitializedSubtaskObject() {
        Subtask subtask2 = new Subtask("Test Subtask 2", "Test Subtask 2 Description");

        assertNotNull(subtask2);
        assertEquals("Test Subtask 2", subtask2.getName());
        assertEquals("Test Subtask 2 Description", subtask2.getDescription());
        assertEquals(Status.NEW, subtask2.getStatus());
        assertEquals(0, subtask2.getId());
        assertNull(subtask2.getEpic());
    }

    @Test
    void copy_ShouldReturnSameFieldsOfSubtaskObject() {
        subtask1.setStatus(Status.IN_PROGRESS);
        subtask1.setEpic(epic1);

        Subtask copiedSubtask = subtask1.copy();

        assertEquals(subtask1.getId(), copiedSubtask.getId());
        assertEquals(subtask1.getName(), copiedSubtask.getName());
        assertEquals(subtask1.getDescription(), copiedSubtask.getDescription());
        assertEquals(subtask1.getStatus(), copiedSubtask.getStatus());
        assertEquals(subtask1.getEpic(), copiedSubtask.getEpic());
    }

    @Test
    void assignEpicToSubtaskShouldWorkCorrectly() {
        epic1 = epic1.copy(2);
        subtask1.setEpic(epic1);

        assertEquals(epic1, subtask1.getEpic());

        Epic newEpic = new Epic("New Epic", "Test Epic Description");
        newEpic = newEpic.copy(3);
        subtask1.setEpic(newEpic);

        assertEquals(newEpic, subtask1.getEpic());

        PrintStream originalOut = System.out;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(byteArrayOutputStream);
        System.setOut(out);

        subtask1.setEpic(null);

        System.setOut(originalOut);

        assertEquals("Epic cannot be null." + System.lineSeparator(), byteArrayOutputStream.toString());
    }

    @Test
    void hashCode_shouldWorkCorrectly() {
        Task subtask2 = new Subtask("Test Subtask 2", "Test Subtask 2 Description");
        subtask2 = subtask2.copy(1);

        Task subtask3 = new Subtask("Test Subtask 3", "Test Subtask 3 Description");
        subtask3 = subtask3.copy(2);

        assertEquals(subtask1.hashCode(), subtask2.hashCode(), "Subtasks with same id should return same hash code value");
        assertNotEquals(subtask1.hashCode(), subtask3.hashCode(), "Subtasks with different id should return different hash code values");
    }


    @Test
    void toString_ShouldWorkCorrectly() {
        subtask1.setStatus(Status.NEW);

        String expected1 = "Subtask{id=1, name='Test Subtask 1', description='Test Subtask 1 Description', status='NEW', epicId=}";
        assertEquals(expected1, subtask1.toString());

        subtask1.setEpic(epic1.copy(2));

        String expected2 = "Subtask{id=1, name='Test Subtask 1', description='Test Subtask 1 Description', status='NEW', epicId=2}";
        assertEquals(expected2, subtask1.toString());
    }

}