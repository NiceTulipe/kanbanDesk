package httpServer;


import Utility.Managers;
import com.sun.net.httpserver.HttpServer;
import httpServer.Handle.*;
import managers.TaskManager;



import java.io.IOException;

import java.net.InetSocketAddress;


public class HttpTaskServer {
    private final HttpServer httpServer;
    private static final int PORT = 8080;

    public HttpTaskServer() throws IOException, InterruptedException {
        TaskManager taskManager = Managers.getDefault("http://localhost:"+KVServer.PORT);


        this.httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks/", new PriorityHandle(taskManager));
        httpServer.createContext("/tasks/task/", new TaskHandle(taskManager));
        httpServer.createContext("/tasks/epic/", new EpicHandle(taskManager));
        httpServer.createContext("/tasks/subtask/", new SubtaskHandle(taskManager));
        httpServer.createContext("/tasks/subtask/epic/", new SubtasksForEpicHandle(taskManager));
        httpServer.createContext("/tasks/history/", new HistoryHandle(taskManager));


    }

    public void start() {
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(0);
    }
}