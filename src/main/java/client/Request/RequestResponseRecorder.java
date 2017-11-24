package client.Request;

import config.GlobalConfig;
import persistance.S3JsonReader;
import persistance.S3JsonWriter;

import java.util.Random;

public class RequestResponseRecorder {
    public static final String PATH_FMT = GlobalConfig.RECORDER_PATH_FMT;

    S3JsonWriter _writer;
    S3JsonReader _reader;
    private int _gameweek;
    private int _sequenceId;
    private int _recordId;

    RecordCollection _records = null;

    public RequestResponseRecorder(int gameweek, int recordSequence) {
        _gameweek = gameweek;
        _writer = new S3JsonWriter();
        _reader = new S3JsonReader();
        _sequenceId = recordSequence;
        _recordId = getRecordId();
    }

    public void record(String url, String response) {
        if (_records == null) {
            _records = readExisting();
            if (_records == null) {
                _records = new RecordCollection();
            }
        }

        Record newRecord = new Record();
        newRecord.url = url;
        newRecord.response = response;
        _records.records.put(url, newRecord);
        _writer.write(getObjPath(_sequenceId), _records);
    }

    public RecordCollection readExisting() {
        String key = getObjPath(_sequenceId);
        return _reader.read(key, RecordCollection.class);
    }

    private String getObjPath(int sequenceId) {
        return String.format(PATH_FMT, _gameweek, sequenceId, "responses_" + Integer.toString(_recordId));
    }

    private int getRecordId() {
        Random rand = new Random();
        return rand.nextInt(10000);
    }
}
