package cache;

import data.ProcessedLeagueFixtureList;
import data.eplapi.*;

import java.util.HashMap;

public class FootballerDataCache {
    public HashMap<Integer, Footballer> footballers = new HashMap<Integer, Footballer>();
    public HashMap<Integer, FootballerDetails> footballerDetails = new HashMap<Integer, FootballerDetails>();
    public HashMap<Integer, Live> liveData = new HashMap<>();
    public HashMap<Integer, EntryData> entries = new HashMap<>();
    public HashMap<Integer, TeamHistory> history = new HashMap<>();
    public HashMap<Integer, ProcessedLeagueFixtureList> leagueEntriesAndMatches = new HashMap<>();
    public BootstrapStatic bootstrapStatic = null;

    public void clear() {
        footballers.clear();
        footballerDetails.clear();
        entries.clear();
        liveData.clear();
        leagueEntriesAndMatches = null;
        bootstrapStatic = null;
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
