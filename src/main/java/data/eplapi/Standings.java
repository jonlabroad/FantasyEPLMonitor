package data.eplapi;

import java.util.List;

/**
 * Created by jonla on 9/30/2017.
 */
public class Standings {
    public boolean has_next;
    public int number;
    public StandingsList standings;
    public Matches matches_next;
    public Matches matches_this;

    public Standings(NewStandings ns)
    {
        has_next = ns.new_entries.has_next;
        number = ns.new_entries.number;
        standings = new StandingsList();
        standings.results = new Standing[ns.new_entries.results.size()];
        int i = 0;
        for (NewResult nr : ns.new_entries.results) {
            Standing standing = new Standing();
            standing.entry = nr.entry;
            standing.id = nr.id;
            standing.player_name = nr.player_first_name + " " + nr.player_last_name;
            standing.entry_name = nr.entry_name;
            standings.results[i++] = standing;
        }
    }
}
