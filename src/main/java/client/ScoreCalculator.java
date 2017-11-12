package client;

import cache.DataCache;
import data.eplapi.*;
import data.Score;

import java.lang.reflect.Field;
import java.util.Map;

public class ScoreCalculator {
    public Score Calculate(Picks picks, Map<Integer, FootballerDetails> details) {
        // Find the footballers and tally the current score
        Score score = new Score();
        for (int i = 0; i < picks.picks.length; i++) {
            boolean isSub = i >= 11;
            Pick pick = picks.picks[i];
            Footballer footballer = DataCache.footballers.get(pick.element);
            FootballerDetails detail = details.get(pick.element);
            int thisScore = 0;
            Field[] fields = FootballerScoreDetailElement.class.getFields();
            for (FootballerScoreDetail scoreDetail : detail.explain) {
                for (Field field : fields) {
                    try {
                        ScoreExplain explain = (ScoreExplain) field.get(scoreDetail.explain);
                        if (explain.points > 0 || explain.value > 0) {
                            //System.out.format("%s %s %d %d\n", footballer.web_name, field.getName(), explain.value, explain.points);
                        }
                        thisScore += explain.points * pick.multiplier;
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
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