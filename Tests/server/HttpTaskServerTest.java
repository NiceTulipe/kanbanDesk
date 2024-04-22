package server;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import httpServer.HttpTaskServer;
import httpServer.KVServer;
import managers.HttpTaskManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;

import static java.net.HttpURLConnection.HTTP_CREATED;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskServerTest {
    private static final KVServer kvServer;


    static {
        try {
            kvServer = new KVServer();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static HttpTaskServer httpServer;

    private static Gson gson;

    protected Epic testAddEpic() {
        return new Epic("taskepicNAME1", "taskOpisanieepic1");
    }

    protected Task testAddTask() {
        return new Task("taskNAME3", "taskOpisanie3", TaskStatus.NEW,
                60L, LocalDateTime.of(2023, 1, 3, 9, 0));
    }

    protected Subtask testAddSubTask() {
        return new Subtask("taskSUBepicNAME1", "taskOpisanieSUBepic1", TaskStatus.NEW,
                60L, LocalDateTime.of(2023, 1, 2, 0, 0), 1);
    }

    @BeforeAll
    static void setUp() throws IOException, InterruptedException {
        kvServer.start();
        httpServer = new HttpTaskServer();
        httpServer.start();
        gson = new Gson();
    }

    @AfterAll
    static void shutDown() {
        httpServer.stop();
        kvServer.stop();
    }

    @Test
    public void loadFromKVServer_CorrectLoad() throws IOException, InterruptedException {
        HttpTaskManager taskManager = new HttpTaskManager("http://localhost:"+KVServer.PORT);

        Task task1 = new Task("taskNAME1", "taskOpisanie1", TaskStatus.NEW,
                60L, LocalDateTime.of(2023, 1, 1, 0, 0));
        Task task2 = new Task("taskNAME2", "taskOpisanie2", TaskStatus.IN_PROGRESS,
                60L, LocalDateTime.of(2023, 1, 2, 8, 0));
        Task task3 = new Task("taskNAME3", "taskOpisanie3", TaskStatus.DONE,
                60L, LocalDateTime.of(2023, 1, 3, 9, 0));

        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);
        taskManager.addNewTask(task3);

        int sizeMapTakBeforeLoad = taskManager.getTasks().size();
        int sizeMapEpicBeforeLoad = taskManager.getEpics().size();

        System.out.println("Epics " + sizeMapEpicBeforeLoad + " Tasks " + sizeMapTakBeforeLoad);

        HttpTaskManager taskManagerLoad = new HttpTaskManager("http://localhost:8078");

        int sizeMapTakAfterLoad = taskManagerLoad.getTasks().size();
        int sizeMapEpicAfterLoad = taskManagerLoad.getEpics().size();

        Assertions.assertEquals(sizeMapTakBeforeLoad, sizeMapTakAfterLoad, "Загрузка Задач прошла неудачно!");
        Assertions.assertEquals(sizeMapEpicBeforeLoad, sizeMapEpicAfterLoad, "Загрузка Эпиков прошла неудачно!");



    }


    @Test
    void addTasksToTaskServerOrUpdate() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        Epic epic = testAddEpic();
        Subtask sub = testAddSubTask();
        Task task = testAddTask();




        URI url = URI.create("http://localhost:8080/tasks/task/");
        String json = gson.toJson(task);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(HTTP_CREATED, response.statusCode());
        body = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(HTTP_CREATED, response.statusCode());

        url = URI.create("http://localhost:8080/tasks/epic/");
        json = gson.toJson(epic);
        body = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response1 = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(HTTP_CREATED, response1.statusCode());

        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        response1 = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(218, response1.statusCode());


    }

    @Test
    void deleteAllTasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());


        url = URI.create("http://localhost:8080/tasks/task/");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        JsonArray arrayTasks = JsonParser.parseString(response.body()).getAsJsonArray();
        assertEquals(0, arrayTasks.size());

    }





}
