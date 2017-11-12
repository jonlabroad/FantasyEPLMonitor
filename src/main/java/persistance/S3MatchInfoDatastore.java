package persistance;

import config.GlobalConfig;
import data.MatchInfo;

public class S3MatchInfoDatastore implements IMatchInfoDatastore {

    private S3JsonReader _reader;
    private S3JsonWriter _writer;
    private static final String KEY_PATH_FORMAT = "%s/%d/%d/%d";
    private static final String CURRENT_KEY_FORMAT = KEY_PATH_FORMAT + "/" + "MatchInfo";
    private static final String NEXT_KEY_FORMAT = KEY_PATH_FORMAT + "/" + "ScoutingReport";

    private int _leagueId;

    public S3MatchInfoDatastore(int leagueId) {
        init(leagueId);
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

    public void delete(int teamId, int eventId) {
        _writer.delete(createNextKey(teamId, eventId));
        _writer.delete(createCurrentKey(teamId, eventId));
    }

    private void init(int leagueId) {
        _reader = new S3JsonReader();
        _writer = new S3JsonWriter();
        _leagueId = leagueId;
    }

    private MatchInfo readInfo(String keyName) {
        return _reader.read(keyName, MatchInfo.class);
    }

    private String createCurrentKey(int teamId, int eventId) {
        return String.format(CURRENT_KEY_FORMAT, GlobalConfig.MatchInfoRoot, _leagueId, teamId, eventId);
    }

    private String createNextKey(int teamId, int eventId) {
        return String.format(NEXT_KEY_FORMAT, GlobalConfig.MatchInfoRoot, _leagueId, teamId, eventId);
    }
}
