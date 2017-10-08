package Cache;

import Data.EPLAPI.Footballer;
import Data.EPLAPI.FootballerDetails;

import java.util.HashMap;

public class DataCache {
    public static HashMap<Integer, Footballer> footballers = new HashMap<Integer, Footballer>();
    public static HashMap<Integer, FootballerDetails> footballerDetails = new HashMap<Integer, FootballerDetails>();
}
