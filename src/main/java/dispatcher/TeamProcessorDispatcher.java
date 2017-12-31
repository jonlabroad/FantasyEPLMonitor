package dispatcher;

import client.EPLClient;
import client.EPLClientFactory;
import data.ProcessedTeam;
import processor.player.ProcessedPlayerProvider;
import processor.team.SingleTeamProcessor;
import util.ParallelExecutor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class TeamProcessorDispatcher {
    ParallelExecutor _executor;
    EPLClient _client;
    Collection<Integer> _teamIds;
    int _gameweek;
    ArrayList<SingleTeamProcessor> _processors = new ArrayList<>();

    public TeamProcessorDispatcher(EPLClient client, Collection<Integer> teamIds, int gameweek) {
        _client = client != null ? client : EPLClientFactory.createClient();
        _executor = new ParallelExecutor();
        _teamIds = teamIds;
        _gameweek = gameweek;
    }

    public void start() {
        for (int teamId : _teamIds) {
            ProcessedPlayerProvider playerProvider = new ProcessedPlayerProvider();
            SingleTeamProcessor processor = new SingleTeamProcessor(playerProvider, teamId, _gameweek, _client);
            _processors.add(processor);
            _executor.add(processor);
        }
        _executor.start();
    }

    public HashMap<Integer, ProcessedTeam> join() {
        HashMap<Integer, ProcessedTeam> teams = new HashMap<>();
        _executor.join();
        for (SingleTeamProcessor processor : _processors) {
            ProcessedTeam team = processor.getResult();
            if (team == null) {
                System.out.format("No results found from team processor %d\n", processor.getId());
            }
            else {
                teams.put(processor.getId(), team);
            }
        }
        _executor.close();
        return teams;
    }
}
