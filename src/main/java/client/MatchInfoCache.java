package client;

import client.Request.MatchInfoCacheKey;
import data.LegacyMatchInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class MatchInfoCache {
    private int _leagueId;
    private HashMap<MatchInfoCacheKey, LegacyMatchInfo> _matchInfoCacheByGameweek = new HashMap<>();
    private HashMap<Integer, LegacyMatchInfo> _thisMatchByTeamId = new HashMap<>();
    private HashMap<Integer, LegacyMatchInfo> _nextMatchByTeamId = new HashMap<>();

    public MatchInfoCache(int leagueId) {
        // This should not be constant if multiple leagues are ever supported
        _leagueId = leagueId;
    }

    public LegacyMatchInfo getMatchInfo(int teamId, int gameweek) {
        return _matchInfoCacheByGameweek.get(createCacheKey(teamId,gameweek));
    }

    public LegacyMatchInfo getCurrentMatchInfo(int teamId) {
        return _thisMatchByTeamId.get(teamId);
    }

    public LegacyMatchInfo getNextMatchInfo(int teamId) {
        return _nextMatchByTeamId.get(teamId);
    }

    public void writeMatchInfo(LegacyMatchInfo info) {
        for (MatchInfoCacheKey key : createCacheKeys(info)) {
            _matchInfoCacheByGameweek.put(key, info);
        }
    }

    public void writeCurrentMatchInfo(LegacyMatchInfo info) {
        writeMatchInfo(info);
        for (int teamId : info.teamIds) {
            _thisMatchByTeamId.put(teamId,info);
        }
    }

    public void writeNextMatchInfo(LegacyMatchInfo info) {
        writeMatchInfo(info);
        for (int teamId : info.teamIds) {
            _nextMatchByTeamId.put(teamId, info);
        }
    }

    private Collection<MatchInfoCacheKey> createCacheKeys(LegacyMatchInfo info) {
        Collection<MatchInfoCacheKey> keys = new ArrayList<>();
        for (int teamId : info.teamIds) {
            keys.add(createCacheKey(teamId, info.match.event));
        }
        return keys;
    }

    private MatchInfoCacheKey createCacheKey(int teamId, int gameweek) {
        return new MatchInfoCacheKey(_leagueId, teamId, gameweek);
    }

}
