package Persistance;

import Data.MatchInfo;

public interface IMatchInfoDatastore {
    MatchInfo ReadLast(int teamId);
    void WriteCurrent(MatchInfo info);
}
