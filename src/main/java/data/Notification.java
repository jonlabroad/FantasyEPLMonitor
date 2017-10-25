package data;

import java.util.List;

public abstract class Notification {
    public String type;
    public String title;
    public String shortDescription;
    public abstract List<MatchEvent> getTickerEvents();
}
