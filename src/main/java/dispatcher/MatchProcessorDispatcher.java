package dispatcher;

import client.EPLClient;
import client.EPLClientFactory;
import data.LiveStandings;
import data.MatchInfo;
import data.ProcessedLeagueFixtureList;
import data.ProcessedTeam;
import data.eplapi.Match;
import data.eplapi.Standings;
import persistance.S3JsonWriter;
import processor.MatchProcessor;
import util.ParallelExecutor;

import java.util.ArrayList;
import java.util.Collection;
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
            new S3JsonWriter().write(String.format("data/%d/api/leagues-h2h-standings", _leagueId), standings, true);
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
            LiveStandings liveStandings = new LiveStandings(matchInfos, _client.getStandings(_leagueId));
            for (MatchInfo matchInfo : matchInfos) {
                try {
                    matchInfo.liveStandings = liveStandings;
                }
                finally {
                    MatchProcessor.writeMatchInfo(_leagueId, matchInfo);
                }
            }
        }

        _executor.close();
        return matchInfos;
    }
}
