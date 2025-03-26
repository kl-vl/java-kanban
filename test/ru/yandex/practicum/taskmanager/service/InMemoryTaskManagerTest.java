package ru.yandex.practicum.taskmanager.service;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.taskmanager.model.Epic;
import ru.yandex.practicum.taskmanager.model.Subtask;
import ru.yandex.practicum.taskmanager.model.Task;
import ru.yandex.practicum.taskmanager.service.exception.InvalidManagerTaskException;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryTaskManagerTest  extends BaseTaskManagerTest<InMemoryTaskManager> {

    @Override
    protected InMemoryTaskManager createTaskManager() {
        return new InMemoryTaskManager();
    }

    @Test
    void testTaskManagerHelperAddInternal() throws InvalidManagerTaskException {
        InMemoryTaskManager.TaskManagerHelper helper = taskManager.getHelper();

        Task taskNew1 = new Task("Task 1", "Description of Task 1");
        final Task task1 = taskNew1.copy(1);
        helper.addInternal(task1);

        assertAll("Task should be added to tasks",
                () -> assertTrue(taskManager.getTasks().contains(task1), "Task should be in the tasks list"),
                () -> assertEquals(task1, taskManager.getTaskById(task1.getId()).orElse(null), "Task should be retrievable by ID")
        );

        Epic epicNew = new Epic("Epic 1", "Description of Epic 1");
        final Epic epic1 = epicNew.copy(2);
        helper.addInternal(epic1);

        assertAll("Epic should be added to epics",
                () -> assertTrue(taskManager.getEpics().contains(epic1), "Epic should be in the epics list"),
                () -> assertEquals(epic1, taskManager.getEpicById(epic1.getId()).orElse(null), "Epic should be retrievable by ID")
        );

        Subtask subtaskNew1 = new Subtask("Subtask 1", "Description of Subtask 1");
        Subtask subtask1 = subtaskNew1.copy(3);
        subtask1.setEpic(epic1);
        helper.addInternal(subtask1);

        assertAll("Subtask should be added to subtasks and linked to epic",
                () -> assertTrue(taskManager.getSubtasks().contains(subtask1), "Subtask should be in the subtasks list"),
                () -> assertEquals(subtask1, taskManager.getSubtaskById(subtask1.getId()).orElse(null), "Subtask should be retrievable by ID"),
                () -> assertTrue(epic1.getSubtasksList().contains(subtask1), "Subtask should be linked to the epic")
        );
    }

    @Test
    void testTaskManagerHelperSetIdCounter() throws InvalidManagerTaskException {
        InMemoryTaskManager.TaskManagerHelper helper = taskManager.getHelper();
        helper.setIdCounter(11);
        Task task = new Task("Task 1", "Description of Task 1");
        int taskId = taskManager.addTask(task);

        assertEquals(11, taskId, "Task ID should start from 11 after setting idCounter");
    }

}