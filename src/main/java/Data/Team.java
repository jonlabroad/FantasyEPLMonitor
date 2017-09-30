package Data;

import Data.EPLAPI.Picks;
import Data.EPLAPI.Standing;

public class Team {
    public int id;
    public String name;
    public String playerName;
    public Picks picks;
    public Standing standing;
    public int currentPoints = 0;
}
