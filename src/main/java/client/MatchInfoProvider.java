package client;

import data.LegacyMatchInfo;
import com.mashape.unirest.http.exceptions.UnirestException;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;

public class MatchInfoProvider {
    int _leagueId;
    EPLClient _client;
    MatchInfoCache _cache;

    public MatchInfoProvider(int leagueId, EPLClient client) {
        _cache = new MatchInfoCache(leagueId);
        _client = client;
        _leagueId = leagueId;
    }

    public LegacyMatchInfo getCurrentMatch(int teamId) throws IOException, UnirestException {
        LegacyMatchInfo info = _client.getMatchInfo(_leagueId, teamId, false);
        _cache.writeCurrentMatchInfo(info);
        return info;
    }

    public LegacyMatchInfo getNextMatch(int teamId) throws IOException, UnirestException {
        LegacyMatchInfo info = _client.getMatchInfo(_leagueId, teamId, true);
        _cache.writeNextMatchInfo(info);
        return info;
    }

    public LegacyMatchInfo getMatchInfo(int teamId, int gameweek) {
        LegacyMatchInfo info = _cache.getMatchInfo(teamId, gameweek);
        if (info == null) {
            System.out.println("Does not support getting arbitrary match info. Only 'current' and 'next' are available");
            throw new NotImplementedException();
        }
        return info;
    }
}
