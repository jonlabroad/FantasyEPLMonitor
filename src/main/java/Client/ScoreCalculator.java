package Client;

import Cache.DataCache;
import Data.EPLAPI.Footballer;
import Data.EPLAPI.Pick;
import Data.EPLAPI.Picks;

public class ScoreCalculator {
    public Score Calculate(Picks picks) {
        // Find the footballers and tally the current score
        Score score = new Score();
        for (int i = 0; i < picks.picks.length; i++) {
            boolean isSub = i >= 11;
            Pick pick = picks.picks[i];
            Footballer footballer = DataCache.footballers.get(pick.element);
            int thisScore = footballer.event_points * pick.multiplier;
            if (!isSub) {
                score.startingScore += thisScore;
            }
            else {
                score.subScore += thisScore;
            }
        }
        return score;
    }
}