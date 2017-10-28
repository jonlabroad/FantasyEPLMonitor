package client.Request;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.google.gson.Gson;
import config.GlobalConfig;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class RequestResponseRecorder {
    public static final String BASE_PATH = "recorder";
    public static final String PATH_FMT = BASE_PATH + "/%d/%d/%s";

    AmazonS3 _s3;
    private int _gameweek = 10; // heh
    private int _sequenceId;

    public RequestResponseRecorder(int gameweek) {
        _s3 = AmazonS3ClientBuilder.defaultClient();
        _sequenceId = getSequenceId();
    }

    public void record(String url, String response) {
        RecordCollection records = readExisting();
        if (records == null) {
            records = new RecordCollection();
        }

        Record newRecord = new Record();
        newRecord.url = url;
        newRecord.response = response;
        records.records.put(url, newRecord);
        _s3.putObject(GlobalConfig.S3Bucket, getObjPath(_sequenceId), new Gson().toJson(records, RecordCollection.class));
    }

    public RecordCollection readExisting() {
        String key = getObjPath(_sequenceId);
        if (_s3.doesObjectExist(GlobalConfig.S3Bucket, key)) {
            S3Object s3Obj = _s3.getObject(GlobalConfig.S3Bucket, key);
            RecordCollection record = readObject(s3Obj, RecordCollection.class);
            return record;
        }
        return null;
    }

    private String getObjPath(int sequenceId) {
        return String.format(PATH_FMT, _gameweek, sequenceId, "responses");
    }

    private <T> T readObject(S3Object obj, Class<T> cls) {
        // Read one text line at a time and display.
        BufferedReader reader = new BufferedReader(new InputStreamReader(obj.getObjectContent()));
        String json = reader.lines().parallel().collect(Collectors.joining("\n"));
        return new Gson().fromJson(json, cls);
    }

    private int getSequenceId() {
        int i = 0;
        while (_s3.doesObjectExist(GlobalConfig.S3Bucket, getObjPath(i))) {
            i++;
        }
        return i;
    }
}
