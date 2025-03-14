package ru.yandex.practicum.taskmanager.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ManagersTest {

    @Test
    void getDefault_shouldReturnNonNullTaskManagerInstance() {
        TaskManager taskManager = Managers.getDefault();

        assertNotNull(taskManager, "TaskManager should not be null");
    }

    @Test
    void getDefaultHistory_shouldReturnNonNullHistoryManagerInstance() {
        HistoryManager historyManager = Managers.getDefaultHistory();

        assertNotNull(historyManager, "HistoryManager should not be null");
    }

}