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

        assertEquals(subtask1, subtask2, "Subtasks with the same ID should be considered equal");
        assertNotEquals(subtask1, subtask3, "Subtasks with different IDs but the same fields should not be considered equal");
        assertNotEquals(subtask1, subtask3, "Subtasks with different id and same fields should not be equals");
        assertNotEquals(null, subtask1, "A subtask should not be considered equal to null");
        assertNotEquals(new Object(), subtask1, "A subtask should not be considered equal to an object of a different type");
    }

    @Test
    void constructor_shouldReturnProperlyInitializedSubtaskObject() {
        Subtask subtask2 = new Subtask("Test Subtask 2", "Test Subtask 2 Description");

        assertNotNull(subtask2, "Subtask object should not be null after initialization");
        assertEquals("Test Subtask 2", subtask2.getName(), "Subtask name should match the provided value");
        assertEquals("Test Subtask 2 Description", subtask2.getDescription(), "Subtask description should match the provided value");
        assertEquals(Status.NEW, subtask2.getStatus(), "Subtask status should be NEW after initialization");
        assertEquals(0, subtask2.getId(), "Subtask ID should be 0 for a newly created object");
        assertNull(subtask2.getEpic(), "Subtask should not be associated with an epic after initialization");
    }

    @Test
    void copy_ShouldReturnSameFieldsOfSubtaskObject() {
        subtask1.setStatus(Status.IN_PROGRESS);
        subtask1.setEpic(epic1);

        Subtask copiedSubtask1 = subtask1.copy();

        assertEquals(subtask1.getId(), copiedSubtask1.getId(), "Copied Subtask ID should match the original subtask ID");
        assertEquals(subtask1.getName(), copiedSubtask1.getName(), "Copied Subtask name should match the original subtask name");
        assertEquals(subtask1.getDescription(), copiedSubtask1.getDescription(), "Copied Subtask description should match the original subtask description");
        assertEquals(subtask1.getStatus(), copiedSubtask1.getStatus(), "Copied Subtask status should match the original subtask status");
        assertEquals(subtask1.getEpic(), copiedSubtask1.getEpic(), "Copied Subtask Epic should match the original subtask epic");

        Subtask copiedSubtask2 = subtask1.copy(33);

        assertEquals(33, copiedSubtask2.getId(), "Copied with new id subtask ID should be 33");
        assertNotEquals(subtask1.getId(), copiedSubtask2.getId(), "Copied with new id subtask ID should not match the original subtask ID");
        assertEquals(subtask1.getName(), copiedSubtask2.getName(), "Copied subtask name should match the original subtask name");
        assertEquals(subtask1.getDescription(), copiedSubtask2.getDescription(), "Copied subtask description should match the original subtask description");
        assertEquals(subtask1.getStatus(), copiedSubtask2.getStatus(), "Copied subtask status should match the original subtask status");
        assertEquals(subtask1.getEpic(), copiedSubtask2.getEpic(), "Copied subtask epic should match the original subtask epic");
    }

    @Test
    void assignEpicToSubtaskShouldWorkCorrectly() {
        epic1 = epic1.copy(2);
        subtask1.setEpic(epic1);

        assertEquals(epic1, subtask1.getEpic());

        Epic newEpic = new Epic("New Epic", "Test Epic Description");
        newEpic = newEpic.copy(3);
        subtask1.setEpic(newEpic);

        assertEquals(newEpic, subtask1.getEpic(), "Subtask's Epic should be updated to the new Epic");

        PrintStream originalOut = System.out;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(byteArrayOutputStream);
        System.setOut(out);

        subtask1.setEpic(null);

        System.setOut(originalOut);

        assertEquals("Epic cannot be null." + System.lineSeparator(), byteArrayOutputStream.toString(),
                "Setting a null Epic to Subtask should print an error message to the console");
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
        assertEquals(expected1, subtask1.toString(), "The toString() method should return the correct string representation of the Subtask object without Epic");

        subtask1.setEpic(epic1.copy(2));

        String expected2 = "Subtask{id=1, name='Test Subtask 1', description='Test Subtask 1 Description', status='NEW', epicId=2}";
        assertEquals(expected2, subtask1.toString(), "toString() method should return the correct string representation of the Subtask object with Epic");
    }

}