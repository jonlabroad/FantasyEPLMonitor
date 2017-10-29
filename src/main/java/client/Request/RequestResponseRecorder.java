package client.Request;

import config.GlobalConfig;
import persistance.S3JsonReader;
import persistance.S3JsonWriter;

public class RequestResponseRecorder {
    public static final String PATH_FMT = GlobalConfig.RECORDER_PATH_FMT;

    S3JsonWriter _writer;
    S3JsonReader _reader;
    private int _gameweek = 10; // heh
    private int _sequenceId;

    public RequestResponseRecorder(int gameweek) {
        _gameweek = gameweek;
        _writer = new S3JsonWriter();
        _reader = new S3JsonReader();
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
        _writer.write(getObjPath(_sequenceId), records);
    }

    public RecordCollection readExisting() {
        String key = getObjPath(_sequenceId);
        return _reader.read(key, RecordCollection.class);
    }

    private String getObjPath(int sequenceId) {
        return String.format(PATH_FMT, _gameweek, sequenceId, "responses");
    }

    private int getSequenceId() {
        int i = 0;
        while (_reader.doesObjectExist(getObjPath(i))) {
            i++;
        }
        return i;
    }
}
