package Cache;

import Client.EPLClient;
import Data.EPLAPI.Footballer;
import Data.EPLAPI.FootballerDetails;
import Data.EPLAPI.Pick;
import Data.EPLAPI.Picks;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.io.IOException;
import java.util.ArrayList;

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

    public void ReadFootballerDetails(ArrayList<Picks> eventPicks) throws IOException, UnirestException {
        for (Picks picks : eventPicks) {
            for (Pick pick : picks.picks) {
                if (!DataCache.footballerDetails.containsKey(pick.element)) {
                    FootballerDetails detail = _client.GetFootballerDetails(pick.element);
                    DataCache.footballerDetails.put(pick.element, detail);
                }
            }
        }
    }
}
