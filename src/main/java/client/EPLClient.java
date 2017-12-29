package client;

import cache.FootballerDataCache;
import client.Request.EPLRequestGenerator;
import client.Request.IRequestExecutor;
import data.eplapi.*;
import data.Score;
import data.Team;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class EPLClient
{
    private EPLRequestGenerator _generator;
    private IRequestExecutor _executor;
    private FootballerDataCache _footballerCache;

    public EPLClient(IRequestExecutor executor) throws IOException {
        initialize(executor);
    }

    public Standings getStandings(int leagueId) {
        try {
            HttpRequest request = _generator.GenerateLeagueH2hStandingsRequest(leagueId);
            return _executor.Execute(request, Standings.class);
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public HashMap<Integer, Footballer> getFootballers() {
        if (_footballerCache.footballers.size() <= 0) {
            HttpRequest request = _generator.GenerateFootballersRequest();
            Bootstrap bootstrap = _executor.Execute(request, Bootstrap.class);
            _footballerCache.setFootballers(bootstrap.elements);
        }
        return _footballerCache.footballers;
    }

    public BootstrapStatic getBootstrapStatic() {
        if (_footballerCache.bootstrapStatic == null) {
            HttpRequest request = _generator.GenerateBootstrapStaticRequest();
            BootstrapStatic bootstrap = _executor.Execute(request, BootstrapStatic.class);
            _footballerCache.bootstrapStatic = bootstrap;
        }
        return _footballerCache.bootstrapStatic;
    }

    public EntryData getEntry(int teamId) {
        if (!_footballerCache.entries.containsKey(teamId)) {
            EntryData data = readEntry(teamId);
            _footballerCache.entries.put(teamId, data);
        }
        return _footballerCache.entries.get(teamId);
    }

    public HashMap<Integer, LeagueEntriesAndMatches> getLeagueEntriesAndMatches(int leagueId) {
        if (!_footballerCache.leagueEntriesAndMatches.containsKey(leagueId)) {
            HashMap<Integer, LeagueEntriesAndMatches> data = readLeagueH2hMatches(leagueId);
            _footballerCache.leagueEntriesAndMatches.put(leagueId, data);
        }
        return _footballerCache.leagueEntriesAndMatches.get(leagueId);
    }

    public Live getLiveData(int eventId) {
        if (!_footballerCache.liveData.containsKey(eventId)) {
            Live data = readLiveEventData(eventId);
            _footballerCache.liveData.put(eventId, data);
        }
        return _footballerCache.liveData.get(eventId);
    }

    public FootballerDetails readFootballerDetails(int footballerId) throws IOException, UnirestException {
        HttpRequest request = _generator.GenerateFootballerDetailRequest(footballerId);
        FootballerDetails details = _executor.Execute(request, FootballerDetails.class);
        return details;
    }

    public HashMap<Integer, FootballerDetails> getFootballerDetails(Set<Integer> ids) throws IOException, UnirestException {
        return readFootballerDetails(ids);
    }

    public HashMap<Integer, FootballerDetails> readFootballerDetails(Set<Integer> ids) throws IOException, UnirestException {
        for (int id : ids) {
            FootballerDetails detail = getCachedDetails(id);
            if (detail == null) {
                _footballerCache.footballerDetails.put(id, readFootballerDetails(id));
            }
        }
        return _footballerCache.footballerDetails;
    }

    public void readFootballerDetails(Picks picks) throws IOException, UnirestException {
        HashSet<Integer> ids = new HashSet<>();
        for (Pick pick : picks.picks) {
            ids.add(pick.element);
        }
        readFootballerDetails(ids);
    }

    public Live readLiveEventData(int eventId) {
        HttpRequest request = _generator.GenerateLiveDataRequest(eventId);
        Live live = _executor.Execute(request, Live.class);
        return live;
    }

    public EntryData readEntry(int teamId) {
        if (teamId > 0) {
            HttpRequest request = _generator.GenerateEntryRequest(teamId);
            EntryData data = _executor.Execute(request, EntryData.class);
            data.entry.parseKit();
            return data;
        }
        return null;
    }

    public HashMap<Integer, LeagueEntriesAndMatches> readLeagueH2hMatches(int leagueId) {
        HashMap<Integer, LeagueEntriesAndMatches> ret = new HashMap<>();
        int pageNum = 1;
        boolean cont = true;
        do {
            LeagueEntriesAndMatches matches = readLeagueH2hMatches(leagueId, pageNum);
            ret.put(pageNum, matches);
            pageNum++;
            cont = matches != null && matches.matches.has_next;
        } while(cont);
        return ret;
    }

    public LeagueEntriesAndMatches readLeagueH2hMatches(int leagueId, int pageNum) {
        HttpRequest request = _generator.GenerateLeagueH2hMatchesRequest(leagueId, pageNum);
        try {
            return _executor.Execute(request, LeagueEntriesAndMatches.class);
        }
        catch (Exception ex) {
            return null;
        }
    }

    public Picks getPicks(int teamId, int eventId) {
        HttpRequest request = _generator.GeneratePicksRequest(teamId, eventId);
        try {
            return _executor.Execute(request, Picks.class);
        }
        catch (Exception ex) {
            return null;
        }
    }

    private HashMap<Integer, Footballer> getCachedFootballers() {
        return _footballerCache.footballers;
    }

    private FootballerDetails getCachedDetails(int id) {
        return _footballerCache.footballerDetails.get(id);
    }

    private Live getCachedLiveData(int eventId) {
        return _footballerCache.liveData.get(eventId);
    }

    public Match findMatch(Standings standings, int teamId, boolean next) {
        Matches matches = next ? standings.matches_next : standings.matches_this;
        for (Match match : matches.results) {
            if (match.entry_1_entry == teamId || match.entry_2_entry == teamId) {
                return match;
            }
        }
        return null;
    }

    public Match findMatch(int leagueId, int teamId, int gameweek) {
        HashMap<Integer, LeagueEntriesAndMatches> matches = getLeagueEntriesAndMatches(leagueId);
        for (LeagueEntriesAndMatches match : matches.values()) {
            for (Match m : match.matches.results) {
                if (m.event == gameweek && (m.entry_1_entry == teamId || m.entry_2_entry == teamId)) {
                    return m;
                }
            }
        }
        return null;
    }

    private Standing findStanding(Standings standings, int teamId) {
        for (Standing standing : standings.standings.results) {
            if (standing.entry == teamId) {
                return standing;
            }
        }
        return null;
    }

    private void initialize(IRequestExecutor executor) throws IOException {
        _generator = new EPLRequestGenerator();
        _executor = executor;
        _footballerCache = new FootballerDataCache();
    }
}
