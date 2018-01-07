package processor;

import client.EPLClient;
import client.EPLClientFactory;
import client.MatchInfoProvider;
import config.GlobalConfig;
import data.*;
import data.eplapi.*;
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
        Standings standings = _leagueId > 0 ? _client.getStandings(_leagueId) : null;

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

        List<TeamMatchEvent> sharedEvents = new MatchEventDeduplicator().deduplicate(team1, team2);
        sharedEvents.sort(new MatchEventSortComparator());
        _result = createMatchInfo(_match, sharedEvents, team1, team2);
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

    protected HashMap<Integer, Fixture> getFixtures(int gameweek) {
        Live liveData = _client.getLiveData(gameweek);
        if (liveData != null) {
            return organizeFixtures(liveData);
        }
        return new HashMap<>();
    }

    protected HashMap<Integer, Fixture> organizeFixtures(Live liveData) {
        return new HashMap<Integer, Fixture>(); //TODO
    }

    protected MatchInfo createMatchInfo(Match match, List<TeamMatchEvent> events, ProcessedMatchTeam team1, ProcessedMatchTeam team2) {
        MatchInfo info = new MatchInfo(match.event, events, team1, team2, getFixtures(match.event));
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
