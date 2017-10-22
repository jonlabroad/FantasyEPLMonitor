package Data;

import Data.EPLAPI.Footballer;
import Data.EPLAPI.FootballerDetails;
import Data.EPLAPI.Picks;
import Data.EPLAPI.Standing;

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
