package httpServer.Handle;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.TaskManager;
import static java.net.HttpURLConnection.*;

import java.io.IOException;

import static httpServer.Handle.WriteResponse.writeResponse;

public class HistoryHandle implements HttpHandler {

    TaskManager taskManager;
    private final Gson gson = new Gson();
    String response;

    public HistoryHandle(TaskManager newTaskManager) {
        this.taskManager = newTaskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        switch (method) {
            case "GET":
                getHistory(exchange);
                break;
            default:
                writeResponse(exchange, "Такого операции не существует", 404);

        }
    }

    private void getHistory(HttpExchange exchange) throws IOException {
        if (taskManager.getHistoryTasks().isEmpty()) {
            writeResponse(exchange, "История пуста!", HTTP_OK);
        } else {
            response = gson.toJson(taskManager.getHistoryTasks());
            writeResponse(exchange, response, HTTP_OK);
        }
    }
}
