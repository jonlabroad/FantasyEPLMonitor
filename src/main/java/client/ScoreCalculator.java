package client;

import com.mashape.unirest.http.exceptions.UnirestException;
import data.ProcessedPick;
import data.eplapi.*;
import data.Score;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

public class ScoreCalculator {

    public Score calculate(List<ProcessedPick> processedPicks) {
        // Find the footballers and tally the current score
        Score score = new Score();
        for (int i = 0; i < processedPicks.size(); i++) {
            boolean isSub = i >= 11;
            ProcessedPick processedPick = processedPicks.get(i);
            Pick pick = processedPick.pick;
            FootballerScoreDetailElement explains = processedPick.footballer.rawData.explains;
            int thisScore = calculateFootballerScore(explains) * pick.multiplier;
            if (!isSub) {
                score.startingScore += thisScore;
            }
            else {
                score.subScore += thisScore;
            }
        }
        return score;
    }

    public Score Calculate(Picks picks, Map<Integer, Footballer> footballers, Map<Integer, FootballerScoreDetailElement> explains) throws IOException, UnirestException {
        // Find the footballers and tally the current score
        Score score = new Score();
        for (int i = 0; i < picks.picks.length; i++) {
            boolean isSub = i >= 11;
            Pick pick = picks.picks[i];
            Footballer footballer = footballers.get(pick.element);
            FootballerScoreDetailElement explain = explains.get(pick.element);
            int thisScore = calculateFootballerScore(explain) * pick.multiplier;
            if (!isSub) {
                score.startingScore += thisScore;
            }
            else {
                score.subScore += thisScore;
            }
        }
        return score;
    }

    public int calculateFootballerScore(FootballerScoreDetailElement explains) {
        int score = 0;
        Field[] fields = FootballerScoreDetailElement.class.getFields();
        for (Field field : fields) {
            try {
                ScoreExplain explain = (ScoreExplain) field.get(explains);
                score += explain.points;
            } catch (IllegalAccessException e) {
                   e.printStackTrace();
            }
        }
        return score;
    }
}