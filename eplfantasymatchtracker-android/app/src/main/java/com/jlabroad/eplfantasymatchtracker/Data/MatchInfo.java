package com.jlabroad.eplfantasymatchtracker.Data;

import com.jlabroad.eplfantasymatchtracker.Data.EPLAPI.Match;
import com.jlabroad.eplfantasymatchtracker.Data.EPLAPI.Picks;

import java.util.ArrayList;
import java.util.HashMap;

public class MatchInfo {
    public Match match;
    public ArrayList<Picks> picks = new ArrayList<Picks>();
    public HashMap<Integer, Team> teams = new HashMap<Integer, Team>();
}
