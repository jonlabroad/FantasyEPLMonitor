package Persistance;

import Data.MatchInfo;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class S3MatchInfoDatastore implements IMatchInfoDatastore {

    private AmazonS3 _client;
    private static final String BUCKET_NAME = "fantasyeplmatchtracker";
    private static final String KEY_FORMAT = "MatchInfo_%d";

    private int _teamId;

    public S3MatchInfoDatastore(int teamId) {
        _teamId = teamId;
        _client = AmazonS3ClientBuilder.defaultClient();
    }

    public MatchInfo ReadLast(int teamId) {
        if (_client.doesObjectExist(BUCKET_NAME, CreateKey())) {
            S3Object s3Obj = _client.getObject(BUCKET_NAME, CreateKey());
            String json = ReadObject(s3Obj);
            return new Gson().fromJson(json, MatchInfo.class);
        }
        return null;
    }

    public void WriteCurrent(MatchInfo info) {
        String json = toJson(info);
        _client.putObject(BUCKET_NAME, CreateKey(), json);
    }

    public String ReadObject(S3Object obj) {
        // Read one text line at a time and display.
        BufferedReader reader = new BufferedReader(new InputStreamReader(obj.getObjectContent()));

        return reader.lines().parallel().collect(Collectors.joining("\n"));
    }

    private String CreateKey() {
        return String.format(KEY_FORMAT, _teamId);
    }

    private String toJson(MatchInfo info) {
        return new Gson().toJson(info);
    }
}
