package persistance;

import data.MatchInfo;

public class S3MatchInfoDatastore implements IMatchInfoDatastore {

    private S3JsonReader _reader;
    private S3JsonWriter _writer;
    private static final String CURRENT_KEY_FORMAT = "MatchInfo_%d_%d";
    private static final String NEXT_KEY_FORMAT = "ScoutingReport_%d_%d";

    public S3MatchInfoDatastore() {
        init();
    }

    public MatchInfo readMatchInfo(int teamId, int eventId) {
        return readInfo(createCurrentKey(teamId, eventId));
    }

    public MatchInfo readNextMatchInfo(int teamId, int eventId) {
        return readInfo(createNextKey(teamId, eventId));
    }

    public void writeCurrent(int teamId, MatchInfo info) {
        _writer.write(createCurrentKey(teamId, info.match.event), info);
    }

    public void writeNext(int teamId, MatchInfo info) {
        _writer.write(createNextKey(teamId, info.match.event), info);
    }

    private void init() {
        _reader = new S3JsonReader();
        _writer = new S3JsonWriter();
    }

    private MatchInfo readInfo(String keyName) {
        return _reader.read(keyName, MatchInfo.class);
    }

    private String createCurrentKey(int teamId, int eventId) {
        return String.format(CURRENT_KEY_FORMAT, teamId, eventId);
    }

    private String createNextKey(int teamId, int eventId) { return String.format(NEXT_KEY_FORMAT, teamId, eventId); }
}
