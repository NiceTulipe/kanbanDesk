package httpServer.Handle;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.TaskManager;
import tasks.Epic;
import static java.net.HttpURLConnection.*;


import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;


import static httpServer.Handle.WriteResponse.writeResponse;

public class EpicHandle implements HttpHandler {

    TaskManager taskManager;
    private final Gson gson = new Gson();
    String response;

    public EpicHandle(TaskManager newTaskManager) {
        this.taskManager = newTaskManager;
    }

    @Override
public void handle(HttpExchange httpExchange) throws IOException {

        String requestMethod = httpExchange.getRequestMethod();
        switch (requestMethod) {

            case "GET":
                getEpic(httpExchange);
                break;
            case "POST":
                addOrUpdateEpic(httpExchange);
                break;
            case "DELETE":
                deleteEpic(httpExchange);
                break;
            default:
                writeResponse(httpExchange, "Такого операции не существует", 404);
        }
    }

    private void getEpic(HttpExchange exchange) throws IOException {
        if (exchange.getRequestURI().getQuery() == null) {
            response = gson.toJson(taskManager.getEpics());
            writeResponse(exchange, response, HTTP_OK);
            return;
        }
        if (getTaskById(exchange).isEmpty()) {
            writeResponse(exchange, "Некорректный идентификатор!", HTTP_BAD_REQUEST);
            return;
        }
        int id = getTaskById(exchange).get();
        if (taskManager.getEpics().contains(taskManager.getEpic(id))) {
            response = gson.toJson(taskManager.getEpic(id));

        } else {
            writeResponse(exchange, "Задач с таким ID не найдена ", HTTP_BAD_REQUEST);
        }
        writeResponse(exchange, response, HTTP_OK);
    }


    private void addOrUpdateEpic(HttpExchange exchange) throws IOException {
        try {
            InputStream json = exchange.getRequestBody();
            String jsonTask = new String(json.readAllBytes(), StandardCharsets.UTF_8);
            Epic epic = gson.fromJson(jsonTask, Epic.class);
            if (epic == null) {
                writeResponse(exchange, "Задача не должна быть пустой!", HTTP_BAD_REQUEST);
                return;
            }

            if ((epic.getId() != null) && taskManager.getEpics().contains(epic)) {
               taskManager.updateEpic(epic);
                writeResponse(exchange, "Эпик обновлен!", 218);
                return;
            }
            taskManager.addNewEpic(epic);
            writeResponse(exchange, "Задача успешно добавлена!", 201);

        } catch (JsonSyntaxException e) {
            writeResponse(exchange, "Получен некорректный JSON", HTTP_BAD_REQUEST);
        }
    }

    private void deleteEpic(HttpExchange exchange) throws IOException {
        if (exchange.getRequestURI().getQuery() == null) {
            taskManager.deleteAllEpics();
            writeResponse(exchange, "Задачи успешно удалены!", HTTP_OK);
            return;
        }
        if (!getTaskById(exchange).isPresent()) {
            return;
        }
        int id = getTaskById(exchange).get();
        if (taskManager.getEpic(id) == null) {
            writeResponse(exchange, "Задач с таким id не найдено!", 404);
            return;
        }
        taskManager.deleteEpic(id);
        writeResponse(exchange, "Задача успешно удалена!", HTTP_OK);
    }


    private Optional<Integer> getTaskById(HttpExchange exchange) {
        String[] parts = exchange.getRequestURI().getQuery().split("=");
        try {
            return Optional.of(Integer.parseInt(parts[1]));
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }

    }


