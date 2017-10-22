package persistance;

import data.MatchInfo;

public interface IMatchInfoDatastore {
    MatchInfo readMatchInfo(int teamId, int eventId);
    MatchInfo readNextMatchInfo(int teamId, int eventId);
    void writeCurrent(int teamId, MatchInfo info);
    void writeNext(int teamId, MatchInfo info);
}
