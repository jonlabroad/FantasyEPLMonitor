package com.jlabroad.eplfantasymatchtracker.data;

import com.jlabroad.eplfantasymatchtracker.data.eplapi.Match;
import com.jlabroad.eplfantasymatchtracker.data.eplapi.Picks;

import java.util.ArrayList;
import java.util.HashMap;

public class MatchInfo {
    public ArrayList<Integer> teamIds = new ArrayList<>();
    public Match match;
    public ArrayList<Picks> picks = new ArrayList<>();
    public HashMap<Integer, Team> teams = new HashMap<>();
    public ArrayList<String> matchEvents = new ArrayList<>();
}
