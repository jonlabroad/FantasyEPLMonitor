package processor;

import client.EPLClient;
import client.EPLClientFactory;
import com.google.gson.Gson;
import com.mashape.unirest.http.exceptions.UnirestException;
import config.GlobalConfig;
import config.PlayerProcessorConfig;
import data.ProcessedPlayer;
import data.ProcessedPlayerCollection;
import data.eplapi.*;
import processor.player.ProcessedPlayerProvider;
import processor.player.SinglePlayerProcessor;
import util.IParallelizableProcess;

import java.io.IOException;
import java.util.*;

public class PlayerProcessor implements IParallelizableProcess {

    private EPLClient _client;
    private int _playerStart = -1;
    private int _playerEnd = -1;

    public PlayerProcessor() {
        initialize(EPLClientFactory.createHttpClient());
    }

    public PlayerProcessor(int start, int end) {
        PlayerProcessorConfig config = PlayerProcessorConfig.getInstance();
        System.out.format("Using configuration: %s\n", new Gson().toJson(config));
        initialize(EPLClientFactory.createHttpClient(config.record, config.recorderSequence));
        _playerStart = start;
        _playerEnd = end;
    }

    public PlayerProcessor(EPLClient client) {
        initialize(client);
    }

    public void process() {
        try {
            // Get all the footballer data required
            HashMap<Integer, Footballer> footballers = getFootballers();
            Set<Integer> players = getFootballersToProcess(footballers);
            HashMap<Integer, ArrayList<FootballerScoreDetailElement>> explains = getLiveExplains(players);

            ProcessedPlayerProvider provider = new ProcessedPlayerProvider();
            ProcessedPlayerCollection playerCollection = new ProcessedPlayerCollection();
            for (int id : players) {
                Footballer footballer = footballers.get(id);
                ArrayList<FootballerScoreDetailElement> gwExplains = explains.get(id);
                Live liveData = _client.getLiveData(GlobalConfig.CloudAppConfig.CurrentGameWeek);
                SinglePlayerProcessor processor = new SinglePlayerProcessor(provider, GlobalConfig.CloudAppConfig.CurrentGameWeek, footballer, gwExplains, liveData);
                ProcessedPlayer player = processor.process();
                playerCollection.players.put(id, player);
            }
            provider.writePlayers(playerCollection);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private Set<Integer> getFootballersToProcess(HashMap<Integer, Footballer> footballers) {
        Set<Integer> players = new HashSet<>();
        if (_playerStart < 0) {
            players = footballers.keySet();
        }
        else {
            if (_playerEnd > footballers.size() - 1) {
                _playerEnd = footballers.size();
            }
            for (int i = _playerStart; i <= _playerEnd; i++) {
                players.add(i);
            }
        }
        return players;
    }

    private HashMap<Integer, Footballer> getFootballers() throws IOException, UnirestException {
        return _client.getFootballers();
    }

    private HashMap<Integer, FootballerDetails> getDetails(Set<Integer> ids) throws IOException, UnirestException {
        return _client.getFootballerDetails(ids);
    }

    private HashMap<Integer, ArrayList<FootballerScoreDetailElement>> getLiveExplains(Set<Integer> ids) throws IOException, UnirestException {
        HashMap<Integer, ArrayList<FootballerScoreDetailElement>> explains = new HashMap<>();
        Live liveData = _client.getLiveData(GlobalConfig.CloudAppConfig.CurrentGameWeek);
        for (int id : ids) {
            LiveElement element = liveData.elements.get(id);
            if (element != null) {
                explains.put(id, liveData.elements.get(id).getExplains());
            }
        }
        return explains;
    }

    private void initialize(EPLClient client) {
        _client = client;
    }
}
