package client.Request;

import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequest;

import java.io.IOException;

public class RequestExecutor implements IRequestExecutor {
    boolean _record;
    RequestResponseRecorder _recorder;

    public RequestExecutor(boolean record) throws IOException {
        _record = record;
        if (_record) {
            _recorder = new RequestResponseRecorder(10);
        }
    }

    public <T> T Execute(HttpRequest request, Class<T> cls) {
        System.out.println(request.getUrl());
        HttpResponse<String> jsonResponse = readResponseString(request);
        T parsedResponse = Parse(jsonResponse.getBody(), cls);

        if (_record) {
            _recorder.record(request.getUrl(), jsonResponse.getBody());
        }

        return parsedResponse;
    }

    private HttpResponse<String> readResponseString(HttpRequest request) {
        try {
            return request.asString();
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }

    private <T> T Parse(String json, Class<T> cls) {
        return new Gson().fromJson(json, cls);
    }
}
