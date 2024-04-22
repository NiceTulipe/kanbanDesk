package managers;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import exceptions.ManagerLoadException;
import httpServer.KVTaskClient;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;

public class HttpTaskManager extends FileBackedTasksManager {
    KVTaskClient kvClient;
    Gson json = new Gson();

    public HttpTaskManager(String URL) throws IOException, InterruptedException {
        this.kvClient = new KVTaskClient(URL);
        loadFromServer();
    }

    @Override
    public void save() {
        try {
            kvClient.put("task", json.toJson(super.getTasks()));
            kvClient.put("epic", json.toJson(super.getEpics()));
            kvClient.put("subtask", json.toJson(super.getSubtasks()));
            kvClient.put("history", json.toJson(super.getHistoryTasks()));
        } catch (IOException | InterruptedException e) {
            throw new ManagerLoadException("Во время запроса произошла ошибка");
        }
    }


    public void  loadFromServer() {
        try {
            JsonArray loadedArray = kvClient.load("task");
            if (loadedArray == null) {
                return;
            }
            for (JsonElement jsonTask : loadedArray) {
                Task loadedTask = json.fromJson(jsonTask, Task.class);
                int id = loadedTask.getId();
                super.tasks.put(id, loadedTask);
            }
            loadedArray = kvClient.load("epic");
            if (loadedArray == null) {
                return;
            }
            for (JsonElement jsonTask : loadedArray) {
                Epic loadedEpic = json.fromJson(jsonTask, Epic.class);
                int id = loadedEpic.getId();
                super.epics.put(id, loadedEpic);
            }
            loadedArray = kvClient.load("subtask");
            if (loadedArray == null) {
                return;
            }
            for (JsonElement jsonTask : loadedArray) {
                Subtask loadedSubTask = json.fromJson(jsonTask, Subtask.class);
                int id = loadedSubTask.getId();
                super.subtasks.put(id, loadedSubTask);
            }
            loadedArray = kvClient.load("history");
            if (loadedArray == null) {
                return;
            }
            for (JsonElement jsonTaskId : loadedArray) {
                if (jsonTaskId == null) {
                    break;
                }
                int loadedId = jsonTaskId.getAsInt();

                if (epics.containsKey(loadedId)) {
                    getEpic(loadedId);
                } else if (tasks.containsKey(loadedId)) {
                    getTask(loadedId);
                } else if (subtasks.containsKey(loadedId)) {
                    getSubtask(loadedId);
                }
            }
        } catch (UnsupportedOperationException e) {
            System.out.println(" ");
        }

    }
}
