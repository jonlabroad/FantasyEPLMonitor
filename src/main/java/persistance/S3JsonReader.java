package persistance;

import com.amazonaws.services.s3.model.S3Object;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class S3JsonReader extends SimpleS3Provider {
    public <T> T read(String keyName, Class<T> cls) {
        if (_client.doesObjectExist(_bucketName, keyName)) {
            S3Object s3Obj = _client.getObject(_bucketName, keyName);
            String json = readObject(s3Obj);
            return new Gson().fromJson(json, cls);
        }
        return null;
    }

    public boolean doesObjectExist(String key) {
        return _client.doesObjectExist(_bucketName, key);
    }

    private String readObject(S3Object obj) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(obj.getObjectContent()));
        return reader.lines().parallel().collect(Collectors.joining("\n"));
    }
}