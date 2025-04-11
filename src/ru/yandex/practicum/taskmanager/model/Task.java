package ru.yandex.practicum.taskmanager.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import static ru.yandex.practicum.taskmanager.service.TaskDeserializer.escapeCsv;

/**
 * Tasks are considered equal only by their ID (for HashMap).
 * Sorting in TreeSet is determined by a separate comparator based on startTime. *
 */
public class Task {
    private final int id;
    private String name;
    private String description;
    private Status status;
    private LocalDateTime startTime;
    private Duration duration;

    public Task(String name, String description) {
        this(0, name, description, Status.NEW, null, Duration.ZERO);
    }

    public Task(String name, String description, LocalDateTime startTime, Duration duration) {
        this(0, name, description, Status.NEW, startTime, duration);
    }

    Task(int id, String name, String description, Status status, LocalDateTime startTime, Duration duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
    }

    Task(int newId, Task other) {
        this.id = newId;
        this.name = other.name;
        this.description = other.description;
        this.status = other.status;
        this.startTime = other.startTime;
        this.duration = other.duration;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Status getStatus() {
        return status;
    }

    // TODO mod access
    public void setStatus(Status status) {
        this.status = status;
    }

    public Type getType() {
        return Type.TASK;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return startTime != null ? startTime.plusMinutes(duration.toMinutes()) : null;
    }

    // TODO access mod
    public Duration getDuration() {
        return duration;
    }

    public Task copy() {
        return new Task(this.id, this);
    }

    public Task copy(int newId) {
        return new Task(newId, this);
    }

    public Task copyWith(String name, String description, Status status, LocalDateTime startTime, Duration duration) {
        return new Task(
                this.id,
                name == null ? this.name : name,
                description == null ? this.description : description,
                status == null ? this.status : status,
                startTime == null ? this.startTime : startTime,
                duration == null ? this.duration : duration
        );
    }

    public static Task createForDeserialization(int id, String name, String description, Status status, LocalDateTime startTime, Duration duration) {
        return new Task(id, name, description, status, startTime, duration);
    }

    public String serializeCsv() {
        String[] fields = getFieldsForSerialization();
        String[] escapedFields = new String[fields.length];
        for (int i = 0; i < fields.length; i++) {
            escapedFields[i] = escapeCsv(fields[i]);
        }
        return String.join(",", escapedFields);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Task{" + getBaseToString() + '}';
    }

    String getBaseToString() {
        return "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", startTime='" + formatDateTime(getStartTime()) + '\'' +
                ", endTime='" + formatDateTime(getEndTime()) + '\'' +
                ", duration=" + formatDuration(getDuration());
    }

    void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    void setDuration(Duration duration) {
        this.duration = duration;
    }

    String[] getFieldsForSerialization() {
        return new String[]{
                String.valueOf(getId()),
                getType().toString(),
                getName(),
                getStatus().toString(),
                getDescription(),
                "",
                formatDateTime(getStartTime()),
                formatDuration(getDuration()),
        };
    }

    private String formatDateTime(LocalDateTime time) {
        return time != null
                ? time.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                : "";
    }

    private String formatDuration(Duration duration) {
        return (duration != null && !duration.isZero())
                ? String.valueOf(duration.toMinutes())
                : "";
    }

}