package ru.yandex.practicum.taskmanager.model;

public class Subtask extends Task {
    private Epic epic;

    public Subtask(String name, String description) {
        super(0, name, description, Status.NEW);
    }

    public Subtask(int newId, Subtask other) {
        super(newId, other);
        this.epic = other.epic;
    }

    @Override
    public Subtask copy() {
        return new Subtask(this.id, this);
    }

    @Override
    public Subtask copy(int newId) {
        return new Subtask(newId, this);
    }

    public Epic getEpic() {
        return epic;
    }

    public void setEpic(Epic epic) {
        if (epic == null) {
            System.out.println("Epic cannot be null.");
            return;
        }
        this.epic = epic;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status='" + getStatus() + '\'' +
                ", epicId=" + ((getEpic() != null) ? getEpic().getId() : "") +
                '}';
    }
}
