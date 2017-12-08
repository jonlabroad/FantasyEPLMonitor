package data;

import org.joda.time.DateTime;

import java.util.Comparator;

public class MatchEventSortComparator implements Comparator<MatchEvent> {

    @Override
    public int compare(MatchEvent o1, MatchEvent o2) {
        DateTime date1 = util.Date.fromString(o1.dateTime);
        DateTime date2 = util.Date.fromString(o2.dateTime);
        return date1.compareTo(date2);
    }
}
