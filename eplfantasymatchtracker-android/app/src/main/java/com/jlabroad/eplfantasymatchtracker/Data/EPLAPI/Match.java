package com.jlabroad.eplfantasymatchtracker.Data.EPLAPI;

public class Match {
    public int id;
    public int entry_1_entry;
    public String entry_1_name;
    public String entry_1_player_name;
    public int entry_2_entry;
    public String entry_2_name;
    public String entry_2_player_name;
    public boolean is_knockout;
    //winner: null,
    //tiebreak: null,
    public boolean own_entry;
    public int entry_1_points;
    public int entry_1_win;
    public int entry_1_draw;
    public int entry_1_loss;
    public int entry_2_points;
    public int entry_2_win;
    public int entry_2_draw;
    public int entry_2_loss;
    public int entry_1_total;
    public int entry_2_total;
    //seed_value: null,
    public int event;
}
