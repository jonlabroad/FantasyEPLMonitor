package client.Request;

import com.google.gson.Gson;
import com.mashape.unirest.request.HttpRequest;
import config.GlobalConfig;
import persistance.FileCachingS3JsonReader;
import persistance.S3JsonReader;

import java.util.HashMap;

public class PlaybackRequestExecutor implements IRequestExecutor {
    private int _gameweek;
    S3JsonReader _reader = new FileCachingS3JsonReader();
    HashMap<Integer, RecordCollection> _recordCache = new HashMap<>();

    public PlaybackRequestExecutor(int gameweek) {
        _gameweek = gameweek;
    }

   @Override
    public <T> T Execute(HttpRequest request, Class<T> cls) {
        int sequenceNumber = GlobalConfig.CurrentPlaybackSequence;
        RecordCollection records = getRecords(sequenceNumber);
        if (records != null) {
            Record record = records.records.get(request.getUrl());
            if (record != null) {
                T parsedObj = parseJson(record.response, cls);
                return parsedObj;
            }
            else {
                System.out.format("Could not find record for sequence %d: %s\n", sequenceNumber, request.getUrl());
            }
        }
        else {
            System.out.format("Could not find record collection for sequence %d\n", sequenceNumber);
        }
        return null;
   }

    private RecordCollection getRecords(int sequenceId) {
        RecordCollection records = _recordCache.get(sequenceId);
        if (records == null) {
            records = _reader.read(getObjPath(sequenceId), RecordCollection.class);
            _recordCache.put(sequenceId, records);
        }
        return records;
    }

    private String getObjPath(int sequenceId) {
        return String.format(GlobalConfig.RECORDER_PATH_FMT, _gameweek, sequenceId, "responses");
    }

    private <T> T parseJson(String json, Class<T> cls) {
        return new Gson().fromJson(json, cls);
    }
}
