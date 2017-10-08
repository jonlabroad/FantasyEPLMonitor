package Persistance;

import Data.MatchInfo;

public interface IMatchInfoDatastore {
    MatchInfo ReadMatchInfo(int teamId, int eventId);
    MatchInfo ReadNextMatchInfo(int teamId, int eventId);
    void WriteCurrent(MatchInfo info);
    void WriteNext(MatchInfo info);
}
