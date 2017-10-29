package persistance;

import com.google.gson.Gson;

public class S3JsonWriter extends SimpleS3Provider {

    public <T> void write(String key, T object) {
        String json = toJson(object);
        _client.putObject(_bucketName, key, json);
    }

    private <T> String toJson(T data) {
        return new Gson().toJson(data);
    }
}
