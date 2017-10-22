package data;

import data.eplapi.Footballer;
import data.eplapi.FootballerDetails;
import data.eplapi.Picks;
import data.eplapi.Standing;

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
