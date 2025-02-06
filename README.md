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

- Subtask knows its Epic.
- Epic knows all its Subtasks

### Statuses of Task 
1. NEW
2. IN_PROGRESS
3. DONE

- Epic changes status only through its Subtasks status change.

### Visualisation of task structure
![img.png](resorces/img.png)

## Manager functions:

- Retrieve a list of tasks by type
- Delete all tasks by type
- Retrieve a task by its ID
- Add a task with a unique sequential ID
- Update a task with the correct ID
- Delete a task by its ID
- Retrieve all subtasks of a specific epic

### Status management rules:
- The manager does not choose the status for a task. The status information is provided to the manager along with the task information.
For epics:
- If an epic has no subtasks or all subtasks have the status NEW, the status should be NEW.
- If all subtasks have the status DONE, the epic is considered complete with the status DONE.
- In all other cases, the status should be IN_PROGRESS.