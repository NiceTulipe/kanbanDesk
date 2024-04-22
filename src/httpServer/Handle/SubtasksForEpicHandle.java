package httpServer.Handle;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.TaskManager;
import static java.net.HttpURLConnection.*;

import java.io.IOException;
import java.util.Optional;

import static httpServer.Handle.WriteResponse.writeResponse;

public class SubtasksForEpicHandle implements HttpHandler {
    TaskManager taskManager;
    private final Gson gson = new Gson();
    String response;

    public SubtasksForEpicHandle(TaskManager newTaskManager) {
        this.taskManager = newTaskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        switch (method) {
            case "GET":
                getEpicSubtasks(exchange);
                break;
            default:
                writeResponse(exchange, "Такого операции не существует", 404);

        }

    }

    private void getEpicSubtasks(HttpExchange exchange) throws IOException {
        if (getTaskId(exchange).isEmpty()) {
            writeResponse(exchange, "Некорректный идентификатор!", HTTP_BAD_REQUEST);
            return;
        }
        int id = getTaskId(exchange).get();
        if (taskManager.getSubTaskForEpic(id) != null) {
            response = gson.toJson(taskManager.getSubTaskForEpic(id));
        } else {
            writeResponse(exchange, "Задач с таким id не найдено!", 404);
            return;
        }
        writeResponse(exchange, response, HTTP_OK);

    }


    private Optional<Integer> getTaskId(HttpExchange exchange) {
        String[] pathParts = exchange.getRequestURI().getQuery().split("=");
        try {
            return Optional.of(Integer.parseInt(pathParts[1]));
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }
}

