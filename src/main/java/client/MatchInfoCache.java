package client;

import client.Request.MatchInfoCacheKey;
import data.MatchInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class MatchInfoCache {
    private int _leagueId;
    private HashMap<MatchInfoCacheKey, MatchInfo> _matchInfoCacheByGameweek = new HashMap<>();
    private HashMap<Integer, MatchInfo> _thisMatchByTeamId = new HashMap<>();
    private HashMap<Integer, MatchInfo> _nextMatchByTeamId = new HashMap<>();

    public MatchInfoCache(int leagueId) {
        // This should not be constant if multiple leagues are ever supported
        _leagueId = leagueId;
    }

    public MatchInfo getMatchInfo(int teamId, int gameweek) {
        return _matchInfoCacheByGameweek.get(createCacheKey(teamId,gameweek));
    }

    public MatchInfo getCurrentMatchInfo(int teamId) {
        return _thisMatchByTeamId.get(teamId);
    }

    public MatchInfo getNextMatchInfo(int teamId) {
        return _nextMatchByTeamId.get(teamId);
    }

    public void writeMatchInfo(MatchInfo info) {
        for (MatchInfoCacheKey key : createCacheKeys(info)) {
            _matchInfoCacheByGameweek.put(key, info);
        }
    }

    public void writeCurrentMatchInfo(MatchInfo info) {
        writeMatchInfo(info);
        for (int teamId : info.teamIds) {
            _thisMatchByTeamId.put(teamId,info);
        }
    }

    public void writeNextMatchInfo(MatchInfo info) {
        writeMatchInfo(info);
        for (int teamId : info.teamIds) {
            _nextMatchByTeamId.put(teamId, info);
        }
    }

    private Collection<MatchInfoCacheKey> createCacheKeys(MatchInfo info) {
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
