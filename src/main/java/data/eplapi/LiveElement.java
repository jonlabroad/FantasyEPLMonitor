package data.eplapi;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class LiveElement {
    public JsonArray explain;
    public LiveElementStats stats;

    public ArrayList<FootballerScoreDetailElement> getExplains() {
        ArrayList<FootballerScoreDetailElement> parsed = new ArrayList<>();
        for (int i = 0; i < explain.size(); i++) {
            JsonArray explainsArray = (JsonArray) explain.get(i);
            Gson gson = new Gson();
            FootballerScoreDetailElement parsedExplains = new FootballerScoreDetailElement();
            for (int j = 0; j < explainsArray.size() - 1; j++) {
                JsonObject explainJson = (JsonObject) explainsArray.get(j);
                for (String fieldName : explainJson.keySet()) {
                    String elementJson = explainJson.get(fieldName).toString();
                    ScoreExplain parsedExplain = gson.fromJson(elementJson, ScoreExplain.class);
                    setField(parsedExplains, fieldName, parsedExplain);
                }
            }
            parsed.add(parsedExplains);
        }
        return parsed;
    }

    private void setField(FootballerScoreDetailElement element, String fieldName, ScoreExplain explain) {
        try {
            Field field = FootballerScoreDetailElement.class.getField(fieldName);
            field.set(element, explain);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


