package client.Request;

import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequest;

import java.io.IOException;

public class RequestExecutor {
    public RequestExecutor() throws IOException {
    }

    public <T> T Execute(HttpRequest request, Class<T> cls) throws IOException, UnirestException {
        HttpResponse<String> jsonResponse = request.asString();
        T parsedResponse = Parse(jsonResponse.getBody(), cls);
        return parsedResponse;
    }

    private <T> T Parse(String json, Class<T> cls) {
        return new Gson().fromJson(json, cls);
    }
}
