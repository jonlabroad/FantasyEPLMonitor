package com.jlabroad.eplfantasymatchtracker.data;

import com.jlabroad.eplfantasymatchtracker.data.eplapi.Footballer;
import com.jlabroad.eplfantasymatchtracker.data.eplapi.FootballerDetails;
import com.jlabroad.eplfantasymatchtracker.data.eplapi.Picks;
import com.jlabroad.eplfantasymatchtracker.data.eplapi.Standing;

import java.util.HashMap;

public class Team {
    public int id;
    public String name;
    public String playerName;
    public Picks picks;
    public Standing standing;
    public Score currentPoints = new Score();
    public HashMap<Integer, Footballer> footballers = new HashMap<Integer, Footballer>();
    public HashMap<Integer, FootballerDetails> footballerDetails = new HashMap<Integer, FootballerDetails>();
}
