package data.eplapi;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.lang.reflect.Field;

public class LiveElement {
    public JsonArray explain;
    public LiveElementStats stats;

    public FootballerScoreDetailElement getExplain() {
        FootballerScoreDetailElement parsed = new FootballerScoreDetailElement();
        if (explain.size() > 0) {
            JsonArray explainsArray = (JsonArray) explain.get(0);
            Gson gson = new Gson();
            for (int i = 0; i < explainsArray.size() - 1; i++) {
                JsonObject explainJson = (JsonObject) explainsArray.get(i);
                for (String fieldName : explainJson.keySet()) {
                    String elementJson = explainJson.get(fieldName).toString();
                    ScoreExplain parsedExplain = gson.fromJson(elementJson, ScoreExplain.class);
                    setField(parsed, fieldName, parsedExplain);
                }
            }
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


