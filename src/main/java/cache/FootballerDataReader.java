package cache;

import client.EPLClient;
import data.eplapi.Footballer;
import data.eplapi.FootballerDetails;
import data.eplapi.Pick;
import data.eplapi.Picks;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FootballerDataReader {
    private EPLClient _client;

    public FootballerDataReader(EPLClient client) {
        _client = client;
    }

    public void ReadFootballers() throws IOException, UnirestException {
        Footballer[] footballers = _client.GetFootballers();
        for (Footballer footballer : footballers) {
            DataCache.footballers.put(footballer.id, footballer);
        }
    }

    public void ReadFootballerDetails(Pick[] eventPicks) throws IOException, UnirestException {
        for (Pick pick : eventPicks) {
                if (!DataCache.footballerDetails.containsKey(pick.element)) {
                    FootballerDetails detail = _client.GetFootballerDetails(pick.element);
                    DataCache.footballerDetails.put(pick.element, detail);
                }
            }
    }
}
