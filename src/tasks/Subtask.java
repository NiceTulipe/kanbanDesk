package tasks;

import java.time.LocalDateTime;
import java.util.Objects;

public class Subtask extends Task {
    public Subtask(String nameTask, String taskDescription, TaskStatus taskStatus, Long duration, LocalDateTime startTime) {
        super(nameTask, taskDescription, taskStatus, duration, startTime);
    }

    public Subtask(Integer id, String nameTask, String taskDescription, TaskStatus status, Long duration, LocalDateTime startTime, Integer epicId) {
        super(id, nameTask, taskDescription, status, duration,startTime);
        this.epicId = epicId;
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }

    protected Integer epicId;

    public Subtask(String nameTask, String taskDescription, TaskStatus taskStatus, Long duration, LocalDateTime startTime, Integer epicId) {
        super(nameTask, taskDescription, taskStatus, duration, startTime);
        this.epicId = epicId;
    }


    public Integer getEpicId() {
        return epicId;
    }

    public void setEpicID(Integer epicId) {
        this.epicId = epicId;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    @Override
    public String toString() {
        return "  DataSUBTASK{"  + "StartTime =" +startTime   + "   id= " + id +
                "; nameTask='" + nameTask + '\'' +
                ", taskDescription='" + taskDescription + '\'' +
                ", taskStatus=" + taskStatus +
                ",    epic_ID = " + epicId +
                ", taskDuration= "+ duration +"  }" + "\n";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Subtask subtask = (Subtask) o;
        return epicId == subtask.epicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(epicId);
    }
}
