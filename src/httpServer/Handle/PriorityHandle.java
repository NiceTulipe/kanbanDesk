package httpServer.Handle;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.TaskManager;
import static java.net.HttpURLConnection.*;

import java.io.IOException;

import static httpServer.Handle.WriteResponse.writeResponse;

public class PriorityHandle implements HttpHandler {
    TaskManager taskManager;

    private final Gson gson = new Gson();
    String response;

    public PriorityHandle(TaskManager newTaskManager) {
        this.taskManager = newTaskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        switch (method) {
            case "GET":
                getTaskPriority(exchange);
                break;
            default:
                writeResponse(exchange, "Такого операции не существует", 404);

        }
    }

    private void getTaskPriority(HttpExchange exchange) throws IOException {
        if (taskManager.getPrioritizedTasks().isEmpty()) {
            writeResponse(exchange, "Задач пока что нет :(", HTTP_OK);
        } else {
            response = gson.toJson(taskManager.getPrioritizedTasks());
            writeResponse(exchange, response, HTTP_OK);
        }
    }


}
