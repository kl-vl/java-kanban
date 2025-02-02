package ru.yandex.practicum.taskmanager.model;

/**
 * Справочник статусов задач
 */
public enum TaskStatus {
    NEW,
    IN_PROGRESS,
    DONE,


    /*
    TODO нужна ли расшифровка для enum?
    NEW("Задача только создана, к её выполнению ещё не приступили"),
    IN_PROGRESS("Над задачей ведётся работа"),
    DONE("Задача выполнена"),
    ;
    */

    /*
    private final String status;

    TaskStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return String.format("TaskType{status : %s}", status) ;
    }
    */


}
