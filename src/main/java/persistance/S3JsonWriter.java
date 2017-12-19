package persistance;

import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.google.gson.Gson;

public class S3JsonWriter extends SimpleS3Provider {

    public <T> void write(String key, T object) {
        write(key, object, false);
    }

    public <T> void write(String key, T object, boolean pub) {
        String json = toJson(object);
        _client.putObject(_bucketName, key, json);
        if (pub) {
            _client.setObjectAcl(_bucketName, key, CannedAccessControlList.PublicRead);
        }
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
