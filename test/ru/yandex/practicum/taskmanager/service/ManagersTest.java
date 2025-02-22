package ru.yandex.practicum.taskmanager.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ManagersTest {

    @Test
    void getDefault_shouldReturnCorrectInMemoryTaskManagerInstance() {
        TaskManager taskManager = Managers.getDefault();

        assertNotNull(taskManager,"TaskManager should not be null");
        assertInstanceOf(InMemoryTaskManager.class, taskManager, "TaskManager should be an instance of InMemoryTaskManager");
    }

    @Test
    void getDefaultHistory_shouldReturnCorrectInMemoryHistoryManagerInstance() {
        HistoryManager historyManager = Managers.getDefaultHistory();

        assertNotNull(historyManager,"HistoryManager should not be null");
        assertInstanceOf(InMemoryHistoryManager.class, historyManager, "HistoryManager should be an instance of InMemoryHistoryManager");
    }
}