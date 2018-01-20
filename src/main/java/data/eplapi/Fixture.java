package data.eplapi;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.lang.reflect.Field;

public class Fixture {
    public int id;
    public String kickoff_time_formatted;
    public boolean started;
    public int event_day;
    public String deadline_time;
    public String deadline_time_formatted;
    public JsonArray stats;
    public EventStats parsedStats;
    public int code;
    public String kickoff_time;
    public int team_h_score;
    public int team_a_score;
    public boolean finished;
    public int minutes;
    public boolean provisional_start_time;
    public boolean finished_provisional;
    public int event;
    public int team_a;
    public int team_h;

    public EventStats getStats() {
        Gson gson = new Gson();
        EventStats parsedStats = new EventStats();
        for (int i = 0; i < stats.size(); i++) {
            JsonObject statObject = (JsonObject) stats.get(i);
            for (String fieldName : statObject.keySet()) {
                String elementJson = statObject.get(fieldName).toString();
                HomeAwayStats parsedExplain = gson.fromJson(elementJson, HomeAwayStats.class);
                setField(parsedStats, fieldName, parsedExplain);
            }
        }
        return parsedStats;
    }

    private void setField(EventStats element, String fieldName, HomeAwayStats explain) {
        try {
            Field field = EventStats.class.getField(fieldName);
            field.set(element, explain);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
