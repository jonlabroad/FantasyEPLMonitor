package data;

import data.eplapi.Match;
import data.eplapi.Picks;

import java.util.ArrayList;
import java.util.HashMap;

public class LegacyMatchInfo {
    public ArrayList<Integer> teamIds = new ArrayList<>();
    public Match match;
    public HashMap<Integer, Team> teams = new HashMap<>();
    public ArrayList<MatchEvent> matchEvents = new ArrayList<>();
}
