package dispatcher;

import client.EPLClient;
import client.EPLClientFactory;
import config.GlobalConfig;
import data.LiveStandings;
import data.MatchInfo;
import data.ProcessedLeagueFixtureList;
import data.ProcessedTeam;
import data.eplapi.Match;
import data.eplapi.Standing;
import data.eplapi.Standings;
import persistance.S3JsonWriter;
import processor.MatchProcessor;
import util.ParallelExecutor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

public class MatchProcessorDispatcher {

    protected EPLClient _client;
    protected int _leagueId;
    protected Collection<Match> _matches = new ArrayList<>();
    protected HashMap<Integer, ProcessedTeam> _teams;
    protected Collection<MatchProcessor> _processors = new ArrayList<>();
    protected ParallelExecutor _executor;

    public MatchProcessorDispatcher(EPLClient client, int leagueId, HashMap<Integer, ProcessedTeam> teams, Collection<Match> matches) {
        _client = client != null ? client : EPLClientFactory.createClient();
        _matches = matches;
        _teams = teams;
        _leagueId = leagueId;
        _executor = new ParallelExecutor();
    }

    public void dispatch() {
        if (_leagueId > 0) {
            Standings standings = _client.getStandings(_leagueId);
            new S3JsonWriter().write(String.format(GlobalConfig.DataRoot + "/%d/api/leagues-h2h-standings", _leagueId), standings, true);
        }

        // Hack for GW 1
        if (_matches == null) {
            Standings standings = _client.getStandings(_leagueId);
            _matches = new ArrayList<Match>();
            for (Standing standing : standings.standings.results) {
                Match fakeMatch = new Match();
                fakeMatch.entry_1_name = standing.entry_name;
                fakeMatch.entry_1_entry = standing.entry;
                fakeMatch.entry_1_player_name = standing.player_name;
                fakeMatch.entry_2_name = standing.entry_name;
                fakeMatch.entry_2_entry = standing.entry;
                fakeMatch.entry_2_player_name = standing.player_name;
                fakeMatch.event = GlobalConfig.CloudAppConfig.CurrentGameWeek;
                _matches.add(fakeMatch);
            }
        }

        for (Match match : _matches) {
            MatchProcessor processor = new MatchProcessor(_client, _leagueId, _teams, match);
            _processors.add(processor);
            _executor.add(processor);
        }
        _executor.start();
    }

    public ArrayList<MatchInfo> join() {
        ArrayList<MatchInfo> matchInfos = new ArrayList<>();
        _executor.join();
        for (MatchProcessor processor : _processors) {
            MatchInfo matchInfo = processor.getResult();
            if (matchInfo == null) {
                System.out.format("Unable to get match info for a particular match\n");
            }
            else {
                matchInfos.add(matchInfo);
            }
        }

        if (_leagueId > 0) {
            // Gross
            if (true) {
                LiveStandings liveStandings = new LiveStandings(matchInfos, _client.getStandings(_leagueId));
                if (liveStandings != null) {
                    Collections.sort(liveStandings.liveStandings);
                }
                for (MatchInfo matchInfo : matchInfos) {
                    try {
                        matchInfo.liveStandings = liveStandings;
                    } finally {
                        MatchProcessor.writeMatchInfo(_leagueId, matchInfo);
                    }
                }
            }
            else
            {
                // GW 1 HACK
                for (MatchInfo matchInfo : matchInfos) {
                    MatchProcessor.writeMatchInfo(_leagueId, matchInfo);
                }
            }
        }

        _executor.close();
        return matchInfos;
    }
}
