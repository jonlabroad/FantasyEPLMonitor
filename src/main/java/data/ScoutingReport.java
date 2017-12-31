package data;

import data.eplapi.Match;
import processor.scouting.Record;

import java.util.HashMap;
import java.util.HashSet;

public class ScoutingReport {
    public int gameweek;
    public Match match;
    public HashMap<Integer, ProcessedTeam> teams = new HashMap<>();
    public HashMap<Integer, Record> simulatedH2h = new HashMap<>();
    public HashSet<Integer> differentials = new HashSet<>();
}
