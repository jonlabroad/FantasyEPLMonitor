package data;

import data.eplapi.League;
import data.eplapi.Match;

import java.util.ArrayList;
import java.util.HashMap;

public class ProcessedLeagueFixtureList {
    public League league;
    public HashMap<Integer, ArrayList<Match>> matches = new HashMap<>();
}
