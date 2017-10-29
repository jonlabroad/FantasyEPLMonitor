package client.Request;

import com.mashape.unirest.request.HttpRequest;

public interface IRequestExecutor {
    <T> T Execute(HttpRequest request, Class<T> cls);
}
