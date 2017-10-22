package persistance;

import config.GlobalConfig;
import data.MatchInfo;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class S3MatchInfoDatastore implements IMatchInfoDatastore {

    private AmazonS3 _client;
    private static final String BUCKET_NAME = GlobalConfig.S3Bucket;
    private static final String CURRENT_KEY_FORMAT = "MatchInfo_%d_%d";
    private static final String NEXT_KEY_FORMAT = "ScoutingReport_%d_%d";

    public S3MatchInfoDatastore() {
        _client = AmazonS3ClientBuilder.defaultClient();
    }

    public MatchInfo readMatchInfo(int teamId, int eventId) {
        return ReadInfo(CreateCurrentKey(teamId, eventId));
    }

    public MatchInfo readNextMatchInfo(int teamId, int eventId) {
        return ReadInfo(CreateNextKey(teamId, eventId));
    }

    public void writeCurrent(int teamId, MatchInfo info) {
        String json = toJson(info);
        _client.putObject(BUCKET_NAME, CreateCurrentKey(teamId, info.match.event), json);
    }

    public void writeNext(int teamId, MatchInfo info) {
        String json = toJson(info);
        _client.putObject(BUCKET_NAME, CreateNextKey(teamId, info.match.event), json);
    }

    public String ReadObject(S3Object obj) {
        // Read one text line at a time and display.
        BufferedReader reader = new BufferedReader(new InputStreamReader(obj.getObjectContent()));
        return reader.lines().parallel().collect(Collectors.joining("\n"));
    }

    private MatchInfo ReadInfo(String keyName) {
        if (_client.doesObjectExist(BUCKET_NAME, keyName)) {
            S3Object s3Obj = _client.getObject(BUCKET_NAME, keyName);
            String json = ReadObject(s3Obj);
            return new Gson().fromJson(json, MatchInfo.class);
        }
        return null;
    }

    private String CreateCurrentKey(int teamId, int eventId) {
        return String.format(CURRENT_KEY_FORMAT, teamId, eventId);
    }
    private String CreateNextKey(int teamId, int eventId) { return String.format(NEXT_KEY_FORMAT, teamId, eventId); }

    private String toJson(MatchInfo info) {
        return new Gson().toJson(info);
    }
}
