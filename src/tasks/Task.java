package tasks;

import java.time.LocalDateTime;

public class Task {
    protected LocalDateTime startTime;

    protected Long duration;
    protected int id;
    protected String nameTask;
    protected String taskDescription;
    protected TaskStatus taskStatus;
    protected TaskType taskType;

    public Task(String nameTask, String taskDescription, TaskStatus taskStatus, Long duration, LocalDateTime startTime) {
        this.nameTask = nameTask;
        this.taskDescription = taskDescription;
        this.taskStatus = taskStatus;
        this.duration = duration;
        this.startTime = startTime;
    }

    public Task(int id, String nameTask, String taskDescription, TaskStatus taskStatus,Long duration, LocalDateTime startTime) {
        this.id = id;
        this.nameTask = nameTask;
        this.taskDescription = taskDescription;
        this.taskStatus = taskStatus;
        this.duration = duration;
        this.startTime = startTime;
    }

    public Task(String nameTask, String taskDescription) {
        this.nameTask = nameTask;
        this.taskDescription = taskDescription;
    }



    public Task(int id, String nameTask, String taskDescription) {
        this.id = id;
        this.nameTask = nameTask;
        this.taskDescription = taskDescription;
    }

    public Task(Integer id, String nameTask, String taskDescription, TaskStatus taskStatus) {
        this.id = id;
        this.nameTask = nameTask;
        this.taskDescription = taskDescription;
        this.taskStatus = taskStatus;
    }

    public Task(String nameTask, String taskDescription, TaskStatus taskStatus) {
        this.nameTask = nameTask;
        this.taskDescription = taskDescription;
        this.taskStatus = taskStatus;
    }

    public Integer getId() {
        return id;
    }

    public Integer setId(int id) {
        this.id = id;
        return id;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }



    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public Long getDuration() {
        return duration;
    }

    public LocalDateTime getEndTime() {
        LocalDateTime endTime;
        if(startTime == null){
            endTime = null;
        } else {
            if (duration == null) {
                duration = 60l;
            }
                endTime = startTime.plusMinutes(duration);

        }
        return endTime;
    }

    public String getNameTask() {
        return nameTask;
    }

    public void setNameTask(String nameTask) {
        this.nameTask = nameTask;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus status) {
        this.taskStatus = status;
    }


    public TaskType getType() {
        return TaskType.TASK;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return
                "  DataTask{" + "StartTime =" +startTime  + "   id= " + id +
                "; nameTask='" + nameTask + '\'' +
                ", taskDescription='" + taskDescription + '\'' +
                ", taskStatus=" + taskStatus +
                ", taskDuration= "+ duration + "\n";
    }


}
