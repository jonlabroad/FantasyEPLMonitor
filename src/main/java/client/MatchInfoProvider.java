package client;

import config.GlobalConfig;
import data.MatchInfo;
import persistance.IMatchInfoDatastore;
import persistance.S3JsonReader;
import persistance.S3JsonWriter;

import java.util.*;

public class MatchInfoProvider implements IMatchInfoDatastore {
        private S3JsonReader _reader;
        private S3JsonWriter _writer;
        private static final String KEY_PATH_FORMAT = "%s/%d/%d/%d";
        private static final String CURRENT_KEY_FORMAT = KEY_PATH_FORMAT + "/" + "MatchInfo";
        private static final String CUP_KEY_FORMAT = "%s/cup/%d/%d" + "/" + "MatchInfo";

        private int _leagueId;

    public MatchInfoProvider(int leagueId) {
        init(leagueId);
    }

    public MatchInfo readCurrent(int teamId, int eventId) {
        return readInfo(createCurrentKey(teamId, eventId));
    }

    public void writeCurrent(int teamId, MatchInfo info) {
        _writer.write(createCurrentKey(teamId, info.gameweek), info, true);
    }

    public void writeCup(int teamId, MatchInfo cup) {
        _writer.write(createCupKey(teamId, cup.gameweek), cup, true);
    }

    public void delete(int teamId, int eventId) {
        _writer.delete(createCurrentKey(teamId, eventId));
    }

    public List<MatchInfo> readAll() {
        Collection<String> keys = _reader.getKeys(String.format("data/%d", _leagueId));
        List<MatchInfo> matchInfos = new ArrayList<>();
        for (String key : keys) {
            if (!key.endsWith(GlobalConfig.CloudAppConfig.CurrentGameWeek + "/MatchInfo")) {
                continue;
            }

            MatchInfo info = readInfo(key);
            if (info != null) {
                matchInfos.add(info);
            }
        }
        return matchInfos;
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

    private String createCupKey(int teamId, int eventId) {
        return String.format(CUP_KEY_FORMAT, GlobalConfig.MatchInfoRoot, teamId, eventId);
    }
}
