package client;

import com.mashape.unirest.http.exceptions.UnirestException;
import data.ProcessedPick;
import data.eplapi.*;
import data.Score;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
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
            ArrayList<FootballerScoreDetailElement> gwExplains = processedPick.footballer.rawData.explains;
            int thisScore = calculateFootballerScore(gwExplains) * pick.multiplier;
            if (!isSub) {
                score.startingScore += thisScore;
            } else {
                score.subScore += thisScore;
            }
        }
        return score;
    }

    public int calculateFootballerScore(ArrayList<FootballerScoreDetailElement> explains) {
        int score = 0;
        Field[] fields = FootballerScoreDetailElement.class.getFields();
        for (FootballerScoreDetailElement gwExplain : explains) {
            for (Field field : fields) {
                try {
                    ScoreExplain explain = (ScoreExplain) field.get(gwExplain);
                    score += explain.points;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return score;
    }
}