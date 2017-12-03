package persistance;


import data.MatchInfo;

public interface IMatchInfoDatastore {
    MatchInfo readCurrent(int teamId, int eventId);
    void writeCurrent(int teamId, MatchInfo info);
}
