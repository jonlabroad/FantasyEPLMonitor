package persistance;

import com.google.gson.Gson;

public class S3JsonWriter extends SimpleS3Provider {

    public <T> void write(String key, T object) {
        String json = toJson(object);
        _client.putObject(_bucketName, key, json);
    }

    public void delete(String key) {
        if (_client.doesObjectExist(_bucketName, key)) {
            _client.deleteObject(_bucketName, key);
        }
    }

    private <T> String toJson(T data) {
        return new Gson().toJson(data);
    }
}
