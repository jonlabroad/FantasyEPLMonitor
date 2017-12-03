package processor;

import client.EPLClient;
import client.EPLClientFactory;
import com.google.gson.Gson;
import com.mashape.unirest.http.exceptions.UnirestException;
import config.GlobalConfig;
import config.PlayerProcessorConfig;
import data.eplapi.Footballer;
import data.eplapi.FootballerDetails;
import processor.player.SinglePlayerProcessor;

import java.io.IOException;
import java.util.*;

public class PlayerProcessor {

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

    public void process() throws IOException, UnirestException {
        // Get all the footballer data required
        HashMap<Integer, Footballer> footballers = getFootballers();
        Set<Integer> players = getFootballersToProcess(footballers);
        HashMap<Integer, FootballerDetails> details = getDetails(players);

        for(int id : players) {
            Footballer footballer = footballers.get(id);
            FootballerDetails detail = details.get(id);
            SinglePlayerProcessor processor = new SinglePlayerProcessor(GlobalConfig.CloudAppConfig.CurrentGameWeek, footballer, detail);
            processor.process();
        }
    }

    private Set<Integer> getFootballersToProcess(HashMap<Integer, Footballer> footballers) {
        Set<Integer> players = new HashSet<>();
        if (_playerStart < 0) {
            players = footballers.keySet();
        }
        else {
            if (_playerEnd > footballers.size() - 1) {
                _playerEnd = footballers.size() - 1;
            }
            for (int i = _playerStart; i <= _playerEnd; i++) {
                players.add(i);
            }
        }
        return players;
    }

    // For testing
    private Set<Integer> getSmallSetOfIds() {
        Set<Integer> players = new HashSet<>();
        for (int i = 1; i <= 20; i++) {
            players.add(i);
        }
        return players;
    }

    private HashMap<Integer, Footballer> getFootballers() throws IOException, UnirestException {
        return _client.getFootballers();
    }

    private HashMap<Integer, FootballerDetails> getDetails(Set<Integer> ids) throws IOException, UnirestException {
        return _client.getFootballerDetails(ids);
    }

    private void initialize(EPLClient client) {
        _client = client;
    }
}
