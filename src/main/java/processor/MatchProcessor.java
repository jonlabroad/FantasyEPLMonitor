package processor;

import client.EPLClient;
import client.EPLClientFactory;
import client.MatchInfoProvider;
import data.MatchInfo;
import data.ProcessedMatchTeam;
import data.ProcessedTeam;
import data.eplapi.Match;
import data.eplapi.Standing;
import data.eplapi.Standings;
import persistance.S3JsonWriter;
import processor.team.MatchEventDeduplicator;
import util.IParallelizableProcess;

import java.util.*;

public class MatchProcessor implements IParallelizableProcess {
    protected EPLClient _client;

    protected Map<Integer, ProcessedTeam> _teams;
    protected Match _match;
    protected int _leagueId = -1;

    protected MatchInfo _result = null;

    public MatchProcessor(EPLClient client, int leagueId, Map<Integer, ProcessedTeam> teams, Match match) {
        _teams = teams;
        _match = match;
        _leagueId = leagueId;
        _client = client != null ? client : EPLClientFactory.createClient();
    }

    public MatchProcessor(Map<Integer, ProcessedTeam> teams, int leagueId) {
        _teams = teams;
        _leagueId = leagueId;

        _client = EPLClientFactory.createClient();
    }

    public void process() {
        Standings standings = null;
        if (_leagueId > 0) {
            standings = _client.getStandings(_leagueId);
            new S3JsonWriter().write(String.format("data/%d/api/leagues-h2h-standings", _leagueId), standings, true);
        }
        ProcessedTeam pTeam1 = _teams.get(_match.entry_1_entry);
        ProcessedTeam pTeam2 = _teams.get(_match.entry_2_entry);

        if (pTeam1 == null) {
            pTeam1 = pTeam2;
        }

        if (pTeam2 == null) {
            pTeam2 = pTeam1;
        }

        ProcessedMatchTeam team1 = new ProcessedMatchTeam(pTeam1, getStanding(standings, _match.entry_1_entry));
        ProcessedMatchTeam team2 = new ProcessedMatchTeam(pTeam2, getStanding(standings, _match.entry_2_entry));

        new MatchEventDeduplicator().deduplicate(team1, team2);

        _result = createMatchInfo(_match, team1, team2);
        writeMatchInfo(_result);
    }

    public MatchInfo getResult() {
        return _result;
    }

    protected Standing getStanding(Standings standings, int teamId) {
        if (standings == null) {
            return null;
        }

        for (Standing standing : standings.standings.results) {
            if (standing.entry == teamId) {
                return standing;
            }
        }
        return null;
    }

    protected MatchInfo createMatchInfo(Match match, ProcessedMatchTeam team1, ProcessedMatchTeam team2) {
        MatchInfo info = new MatchInfo(match.event, team1, team2);
        info.mergeEvents();
        return info;
    }

    protected void writeMatchInfo(MatchInfo info) {
        for (int id : info.teams.keySet()) {
            System.out.format("Writing data for %d\n", id);
            if (_leagueId > 0) {
                new MatchInfoProvider(_leagueId).writeCurrent(id, info);
            }
            else {
                new MatchInfoProvider(_leagueId).writeCup(id, info);
            }
        }
    }
}
