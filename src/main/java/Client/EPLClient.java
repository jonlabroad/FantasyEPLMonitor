package Client;

import Client.Request.EPLRequestGenerator;
import Client.Request.RequestExecutor;
import Data.EPLAPI.*;
import Data.MatchInfo;
import Data.Team;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequest;
import java.io.IOException;
import java.util.HashMap;

public class EPLClient
{
    private EPLRequestGenerator _generator;
    private RequestExecutor _executor;

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
        return _executor.Execute(request, Picks.class);
    }

    public MatchInfo GetMatchInfo(int leagueId, int teamId) throws IOException, UnirestException {
        Standings standings = GetStandings(leagueId);
        Match match = FindMatch(standings, teamId);
        HashMap<Integer, FootballerDetails> footballerDetails = new HashMap<Integer, FootballerDetails>();


        return CreateMatchInfo(standings, match);
    }

    private MatchInfo CreateMatchInfo(Standings standings, Match match) throws IOException, UnirestException {
        MatchInfo matchInfo = new MatchInfo();
        matchInfo.match = match;
        for (int i = 0; i < 2; i++) {
            Team team = new Team();
            team.id = i == 0 ? match.entry_1_entry : match.entry_2_entry;
            team.name = i == 0 ? match.entry_1_name : match.entry_2_name;
            team.playerName = i == 0 ? match.entry_1_player_name : match.entry_2_player_name;
            team.picks = GetPicks(team.id, match.event);
            team.currentPoints = new ScoreCalculator(GetFootballers()).Calculate(team.picks);
            team.standing = FindStanding(standings, team.id);
            team.footballerDetails = new HashMap<Integer, FootballerDetails>();
            for (Pick pick : team.picks.picks) {
                FootballerDetails details = GetFootballerDetails(pick.element);
                team.footballerDetails.put(pick.element, details);
            }
            matchInfo.teams.add(team);
        }
        return matchInfo;
    }

    private Match FindMatch(Standings standings, int teamId) {
        for (Match match : standings.matches_this.results) {
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
        _executor = new RequestExecutor();
    }
}
