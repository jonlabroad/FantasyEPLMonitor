package persistance;


import data.LegacyMatchInfo;

public interface IMatchInfoDatastore {
    LegacyMatchInfo readMatchInfo(int teamId, int eventId);
    LegacyMatchInfo readNextMatchInfo(int teamId, int eventId);
    void writeCurrent(int teamId, LegacyMatchInfo info);
    void writeNext(int teamId, LegacyMatchInfo info);
}
