package persistance;

import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class S3JsonReader extends SimpleS3Provider {
    public <T> T read(String keyName, Class<T> cls) {
        try {
            if (_client.doesObjectExist(_bucketName, keyName)) {
                S3Object s3Obj = _client.getObject(_bucketName, keyName);
                String json = readObject(s3Obj);
                return new Gson().fromJson(json, cls);
                //return new ObjectMapper().readValue(json, cls);
            }
        }
        catch (Exception ex) {
            System.out.format("Could not read %s\n", keyName);
        }
        return null;
    }

    public boolean doesObjectExist(String key) {
        return _client.doesObjectExist(_bucketName, key);
    }

    protected String readObject(S3Object obj) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(obj.getObjectContent()));
        return reader.lines().parallel().collect(Collectors.joining("\n"));
    }

    public Collection<String> getKeys(String path) {
        Set<String> keys = new HashSet<>();
        ObjectListing result = _client.listObjects(_bucketName, path);
        List<S3ObjectSummary> summaries = result.getObjectSummaries();
        for (S3ObjectSummary summary : summaries) {
            keys.add(summary.getKey());
        }
        return keys;
    }
}
