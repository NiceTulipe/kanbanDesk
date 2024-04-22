package tasks;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    protected LocalDateTime endTime;

    protected ArrayList<Integer> subtaskIds = new ArrayList<>();

    public Epic(String nameTask, String taskDescription) {
        super(nameTask, taskDescription);
    }

    public Epic(Integer id, String nameTask, String taskDescription) {
        super(id, nameTask, taskDescription);
    }

    public Epic(Integer id, String nameTask, String taskDescription, TaskStatus status) {
        super(id, nameTask, taskDescription, status);
    }

    public ArrayList<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void addSubTaskId(Integer idTask) {
        subtaskIds.add(idTask);
    }

    public void setSubtaskIds(ArrayList<Integer> subtaskIds) {
        this.subtaskIds = subtaskIds;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    @Override
    public String toString() {
        return "  DataEPIC{"  + "StartTime =" +startTime  +  "   id= " + id +
                "; nameTask='" + nameTask + '\'' +
                ", taskDescription='" + taskDescription + '\'' +
                ", taskStatus=" + taskStatus +
                "   subTasks =   " + subtaskIds +
                "EndTime =  " + endTime +
                ", taskDuration= "+ duration +'}' +"\n";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtaskIds, epic.subtaskIds);
    }
}


