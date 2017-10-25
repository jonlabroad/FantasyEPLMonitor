package data;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;

public class ScoreNotification extends Notification {

    private DateTime _time;
    private String _p1Name;
    private int _p1Score;
    private int _p1SubScore;
    private String _p2Name;
    private int _p2Score;
    private int _p2SubScore;
    List<MatchEvent> scoreChangeEvents = new ArrayList<>();

    public ScoreNotification(DateTime time, int p1Score, int p1SubScore, int p2Score, int p2SubScore, String p1Name, String p2Name) {
        _time = time;
        _p1Score = p1Score;
        _p2Score = p2Score;
        _p1SubScore = p1SubScore;
        _p2SubScore = p2SubScore;
        _p1Name = p1Name;
        _p2Name = p2Name;
    }

    public void addEvent(MatchEvent event) {
        //scoreChangeEvents.add(String.format("%s [%d(%d) - %d(%d)]: %s", timeToString(_time.withZone(DateTimeZone.forID("America/New_York"))), _p1Score, _p1SubScore, _p2Score, _p2SubScore, event));
        scoreChangeEvents.add(event);

        title = String.format("%s %d - %d %s", _p1Name, _p1Score, _p2Score, _p2Name);
        shortDescription = String.format("%d new events!", scoreChangeEvents.size());
        type = "ScoreChange";
    }

    private String timeToString(DateTime time) {
        DateTimeFormatter fmt = DateTimeFormat.forPattern("MM-dd HH:mm");
        return fmt.print(time);
    }

    @Override
    public List<MatchEvent> getTickerEvents() {
        return scoreChangeEvents;
    }
}

