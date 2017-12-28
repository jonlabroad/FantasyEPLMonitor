package data.eplapi;

import java.util.ArrayList;

public class Fixture {
    public int id;
    public String kickoff_time_formatted;
    public boolean started;
    public int event_day;
    public String deadline_time;
    public String deadline_time_formatted;
    public ArrayList<EventStats> stats = new ArrayList<>();
    public int code;
    public String kickoff_time;
    public int team_h_score;
    public int team_a_score;
    public boolean finished;
    public int minutes;
    public boolean provisional_start_time;
    public boolean finished_provisional;
    public int event;
    public int team_a;
    public int team_h;
}
