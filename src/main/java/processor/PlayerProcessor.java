package processor;

import client.EPLClient;
import client.EPLClientFactory;
import com.mashape.unirest.http.exceptions.UnirestException;
import data.eplapi.Footballer;
import data.eplapi.FootballerDetails;
import processor.player.SinglePlayerProcessor;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class PlayerProcessor {

    EPLClient _client;

    HashSet<Integer> players = new HashSet<>();

    public PlayerProcessor() {
        _client = EPLClientFactory.createHttpClient();
        initialize(EPLClientFactory.createHttpClient());
    }

    public PlayerProcessor(EPLClient client) {
        initialize(client);
    }

    public void process() throws IOException, UnirestException {
        Set<Integer> players = new HashSet<>();
        for (int i = 1; i <= 20; i++) {
            players.add(i);
        }

        // Get all the footballer data required
        HashMap<Integer, Footballer> footballers = getFootballers();
        HashMap<Integer, FootballerDetails> details = getDetails(players);

        for(int id : players) {
            Footballer footballer = footballers.get(id);
            FootballerDetails detail = details.get(id);
            SinglePlayerProcessor processor = new SinglePlayerProcessor(footballer, detail, null);
            processor.process();
        }
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
