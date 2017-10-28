package client;

import cache.DataCache;
import client.Request.EPLRequestGenerator;
import client.Request.RequestExecutor;
import config.GlobalConfig;
import data.eplapi.*;
import data.MatchInfo;
import data.Score;
import data.Team;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequest;
import java.io.IOException;
import java.util.HashMap;

public class EPLClient
{
    private EPLRequestGenerator _generator;
    private RequestExecutor _executor;
    private HashMap<Integer, MatchInfoProvider> _matchInfoProviderByLeague = new HashMap<>();

    public EPLClient() throws IOException {
        Initialize();
    }

    public Standings GetStandings(int leagueId) throws IOException, UnirestException {
        HttpRequest request = _generator.GenerateLeagueH2hStandingsRequest(leagueId);
        return _executor.Execute(request, Standings.class);
    }

    public Footballer[] GetFootballers() throws IOException, UnirestException {
        HttpRequest request = _generator.GenerateFootballersRequest();
        Bootstrap bootstrap = _executor.Execute(request, Bootstrap.class);
        return bootstrap.elements;
    }

    public FootballerDetails GetFootballerDetails(int footballerId) throws IOException, UnirestException {
        HttpRequest request = _generator.GenerateFootballerDetailRequest(footballerId);
        FootballerDetails details = _executor.Execute(request, FootballerDetails.class);
        return details;
    }

    public Picks GetPicks(int teamId, int eventId) throws IOException, UnirestException {
        HttpRequest request = _generator.GeneratePicksRequest(teamId, eventId);
        try {
            return _executor.Execute(request, Picks.class);
        }
        catch (Exception ex) {
            return null;
        }
    }

    public MatchInfo GetMatchInfo(int leagueId, int teamId, boolean next) throws IOException, UnirestException {
        Standings standings = GetStandings(leagueId);
        Match match = FindMatch(standings, teamId, next);
        return CreateMatchInfo(standings, match, next);
    }

    private MatchInfo CreateMatchInfo(Standings standings, Match match, boolean isNext) throws IOException, UnirestException {
        MatchInfo matchInfo = new MatchInfo();
        matchInfo.match = match;
        for (int i = 0; i < 2; i++) {
            Team team = new Team();
            team.id = i == 0 ? match.entry_1_entry : match.entry_2_entry;
            matchInfo.teamIds.add(team.id);
            team.name = i == 0 ? match.entry_1_name : match.entry_2_name;
            team.playerName = i == 0 ? match.entry_1_player_name : match.entry_2_player_name;
            int picksEventId = isNext ? match.event - 1 : match.event;
            team.picks = GetPicks(team.id, picksEventId);
            if (team.picks != null) {
                team.currentPoints = !isNext ? new ScoreCalculator().Calculate(team.picks) : new Score();
                team.footballerDetails = new HashMap<>();
                for (Pick pick : team.picks.picks) {
                    FootballerDetails details = GetFootballerDetails(pick.element);
                    DataCache.footballerDetails.put(pick.element, details);
                    team.footballerDetails.put(pick.element, details);
                }
            }
            team.standing = FindStanding(standings, team.id);
            matchInfo.teams.put(team.id, team);
        }
        return matchInfo;
    }

    private Match FindMatch(Standings standings, int teamId, boolean next) {
        Matches matches = next ? standings.matches_next : standings.matches_this;
        for (Match match : matches.results) {
            if (match.entry_1_entry == teamId || match.entry_2_entry == teamId) {
                return match;
            }
        }
        return null;
    }

    private Standing FindStanding(Standings standings, int teamId) {
        for (Standing standing : standings.standings.results) {
            if (standing.entry == teamId) {
                return standing;
            }
        }
        return null;
    }

    private void Initialize() throws IOException {
        _generator = new EPLRequestGenerator();
        _executor = new RequestExecutor(GlobalConfig.Record);
    }
}
