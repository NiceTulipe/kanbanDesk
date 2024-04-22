package httpServer;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import exceptions.ManagerLoadException;
import exceptions.ManagerSaveException;
import static java.net.HttpURLConnection.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class KVTaskClient {

    private final String apiToken;

    private final String URL;

    public KVTaskClient(String urlHome) throws IOException, InterruptedException {
        this.URL = urlHome;
        URI uri = URI.create(URL+"/register");

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .headers("Content-Type", "application/json")
                .build();
        HttpClient httpClient = HttpClient.newHttpClient();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            this.apiToken = response.body();
        }



    public void put(String key, String json) throws IOException, InterruptedException {
        URI uri = URI.create(this.URL + "/save/" + key + "?API_TOKEN=" + apiToken);

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(uri)
                .header("Content-Type", "application/json")
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();
        try {
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8);
            HttpResponse<String> response = httpClient.send(request, handler);
            if (response.statusCode() != 201) {
                System.out.println("Ошибка сохранения! Код ответа: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            throw new ManagerSaveException("Error response");
        }
    }

    public JsonArray load(String key) {

        URI uri = URI.create(this.URL + "/load/" + key + "?API_TOKEN=" + apiToken);
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .header("Content-Type", "application/json")
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();
        JsonArray loadedJsonArray;
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() != HTTP_OK){
                System.out.println("Ошибка загрузки! Код ответа: " + response.statusCode());
                return null;
            }
            loadedJsonArray = JsonParser.parseString(response.body()).getAsJsonArray();
            return loadedJsonArray;
        } catch (IOException | InterruptedException e) {
            throw new ManagerLoadException("Во время запроса произошла ошибка");
        }
    }



}
