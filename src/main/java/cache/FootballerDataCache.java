package cache;

import data.eplapi.Footballer;
import data.eplapi.FootballerDetails;

import java.util.HashMap;

public class FootballerDataCache {
    public HashMap<Integer, Footballer> footballers = new HashMap<Integer, Footballer>();
    public HashMap<Integer, FootballerDetails> footballerDetails = new HashMap<Integer, FootballerDetails>();

    public void clear() {
        footballers.clear();
        footballerDetails.clear();
    }

    public Footballer getFootballer(int id) {
        return footballers.get(id);
    }

    public FootballerDetails getDetails(int id) {
        return footballerDetails.get(id);
    }

    public void setFootballers(Footballer[] footballersArray) {
        for (Footballer footballer : footballersArray) {
            footballers.put(footballer.id, footballer);
        }
    }
}
