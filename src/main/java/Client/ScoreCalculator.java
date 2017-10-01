package Client;

import Data.EPLAPI.Footballer;
import Data.EPLAPI.Pick;
import Data.EPLAPI.Picks;

public class ScoreCalculator {
    private Footballer[] _footballers;

    public ScoreCalculator(Footballer[] footballers) {
        _footballers = footballers;
    }

    public Score Calculate(Picks picks) {
        // Find the footballers and tally the current score
        Score score = new Score();
        for (int i = 0; i < picks.picks.length; i++) {
            boolean isSub = i >= 11;
            Pick pick = picks.picks[i];
            Footballer footballer = FindFootballer(_footballers, pick.element);
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

    public static Footballer FindFootballer(Footballer[] footballers, int elementId) {
        for (Footballer footballer : footballers) {
            if (footballer.id == elementId) {
                return footballer;
            }
        }
        return null;
    }
}