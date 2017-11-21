package client;

import cache.FootballerDataCache;
import client.Request.EPLRequestGenerator;
import client.Request.IRequestExecutor;
import data.eplapi.*;
import data.MatchInfo;
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
    private HashMap<Integer, MatchInfoProvider> _matchInfoProviderByLeague = new HashMap<>();

    public EPLClient(IRequestExecutor executor) throws IOException {
        initialize(executor);
    }

    public Standings getStandings(int leagueId) throws IOException, UnirestException {
        HttpRequest request = _generator.GenerateLeagueH2hStandingsRequest(leagueId);
        return _executor.Execute(request, Standings.class);
    }

    public HashMap<Integer, Footballer> getFootballers() {
        if (_footballerCache.footballers.size() <= 0) {
            HttpRequest request = _generator.GenerateFootballersRequest();
            Bootstrap bootstrap = _executor.Execute(request, Bootstrap.class);
            _footballerCache.setFootballers(bootstrap.elements);
        }
        return _footballerCache.footballers;
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

    public Picks getPicks(int teamId, int eventId) throws IOException, UnirestException {
        HttpRequest request = _generator.GeneratePicksRequest(teamId, eventId);
        try {
            return _executor.Execute(request, Picks.class);
        }
        catch (Exception ex) {
            return null;
        }
    }

    public MatchInfo getMatchInfo(int leagueId, int teamId, boolean next) throws IOException, UnirestException {
        Standings standings = getStandings(leagueId);
        Match match = findMatch(standings, teamId, next);
        return createMatchInfo(standings, match, next);
    }

    private HashMap<Integer, Footballer> getCachedFootballers() {
        return _footballerCache.footballers;
    }

    private FootballerDetails getCachedDetails(int id) {
        return _footballerCache.footballerDetails.get(id);
    }

    private MatchInfo createMatchInfo(Standings standings, Match match, boolean isNext) throws IOException, UnirestException {
        MatchInfo matchInfo = new MatchInfo();
        matchInfo.match = match;
        for (int i = 0; i < 2; i++) {
            Team team = new Team();
            team.id = i == 0 ? match.entry_1_entry : match.entry_2_entry;
            matchInfo.teamIds.add(team.id);
            team.name = i == 0 ? match.entry_1_name : match.entry_2_name;
            team.playerName = i == 0 ? match.entry_1_player_name : match.entry_2_player_name;
            int picksEventId = isNext ? match.event - 1 : match.event;
            team.picks = getPicks(team.id, picksEventId);
            if (team.picks != null) {
                readFootballerDetails(team.picks);
                team.currentPoints = !isNext ? new ScoreCalculator().Calculate(team.picks, _footballerCache.footballers, _footballerCache.footballerDetails) : new Score();
                team.footballerDetails = new HashMap<>();
                for (Pick pick : team.picks.picks) {
                    FootballerDetails details = _footballerCache.footballerDetails.get(pick.element);
                    team.footballerDetails.put(pick.element, details);
                }
            }
            team.standing = findStanding(standings, team.id);
            matchInfo.teams.put(team.id, team);
        }
        return matchInfo;
    }

    private Match findMatch(Standings standings, int teamId, boolean next) {
        Matches matches = next ? standings.matches_next : standings.matches_this;
        for (Match match : matches.results) {
            if (match.entry_1_entry == teamId || match.entry_2_entry == teamId) {
                return match;
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
