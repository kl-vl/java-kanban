# Tasktracker

### Types of tasks
1. Task
2. Subtask
3. Epic

### Each task has fields
1. id
2. Name
3. Description
4. Status
5. Start Time
6. Duration

- Subtask knows its Epic.
- Epic knows all its Subtasks
- Epic duration is sum of its subtasks durations
- Epic start time is start of earliest subtask and end time - end of latest subtask

### Statuses of Task 
1. NEW
2. IN_PROGRESS
3. DONE

- Epic changes status only through its Subtasks status change.

### Visualisation of task structure
![img.png](documentation/images/img.png)

## Manager functions:
- Retrieve a list of tasks by type
- Delete all tasks by type
- Retrieve a task by its ID
- Add a task with a unique sequential ID
- Update a task with the correct ID
- Delete a task by its ID
- Retrieve all subtasks of a specific epic
- Retrieve list of prioritized tasks
- Check intersection of prioritized tasks before add to list

## Aditional storage functions:
- Store task data in CSV file
- Restore in-memory state from CSV file

### Status management rules:
- The manager does not choose the status for a task. The status information is provided to the manager along with the task information.
For epics:
- If an epic has no subtasks or all subtasks have the status NEW, the status should be NEW.
- If all subtasks have the status DONE, the epic is considered complete with the status DONE.
- In all other cases, the status should be IN_PROGRESS.

## Histoty functions:
- Task view History through HistoryManager.
- Retriving Task from TaskManager add task to history.
- History contains unique tasks with last state.
- History remember the order of task views.
- Add to History and delete from History complexity must be O(1).