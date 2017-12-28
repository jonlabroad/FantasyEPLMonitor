package data.eplapi;

import com.google.gson.Gson;

public class Entry {
    public int id;
    public String player_first_name;
    public String player_last_name;
    public int player_region_id;
    public String player_region_name;
    public String player_region_short_iso;
    public int summary_overall_points;
    public int summary_overall_rank;
    public int summary_event_points;
    public int summary_event_rank;
    public int joined_seconds;
    public int current_event;
    public int total_transfers;
    public int total_loans;
    public int total_loans_active;
    public String transfers_or_loans;
    public boolean deleted;
    public boolean email;
    public String joined_time;
    public String name;
    public int bank;
    public int value;
    public String kit;
    public Kit kitParsed;
    public int event_transfers;
    public int event_transfers_cost;
    public int extra_free_transfers;
    //public int strategy: null;
    public int favourite_team;
    public int started_event;
    public int player;

    public void parseKit() {
        kitParsed = new Gson().fromJson(kit, Kit.class);
    }
}
