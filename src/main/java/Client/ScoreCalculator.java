package Client;

import Data.EPLAPI.Footballer;
import Data.EPLAPI.Pick;
import Data.EPLAPI.Picks;

public class ScoreCalculator {
    private Footballer[] _footballers;

    public ScoreCalculator(Footballer[] footballers) {
        _footballers = footballers;
    }

    // No subs!
    public int Calculate(Picks picks) {
        // Find the footballers and tally the current score
        int score = 0;
        for (int i = 0; i < 11; i++) {
            Pick pick = picks.picks[i];
            Footballer footballer = FindFootballer(_footballers, pick.element);
            score += footballer.event_points * pick.multiplier;
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