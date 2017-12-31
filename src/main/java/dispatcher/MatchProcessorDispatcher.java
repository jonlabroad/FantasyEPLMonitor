package dispatcher;

import client.EPLClient;
import client.EPLClientFactory;
import data.MatchInfo;
import data.ProcessedLeagueFixtureList;
import data.ProcessedTeam;
import data.eplapi.Match;
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
        _executor.close();
        return matchInfos;
    }
}
