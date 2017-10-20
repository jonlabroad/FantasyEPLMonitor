package com.jlabroad.eplfantasymatchtracker.Data;

import com.jlabroad.eplfantasymatchtracker.Client.Score;
import com.jlabroad.eplfantasymatchtracker.Data.EPLAPI.Footballer;
import com.jlabroad.eplfantasymatchtracker.Data.EPLAPI.FootballerDetails;
import com.jlabroad.eplfantasymatchtracker.Data.EPLAPI.Picks;
import com.jlabroad.eplfantasymatchtracker.Data.EPLAPI.Standing;

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
