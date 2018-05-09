package data;

import java.util.Comparator;

public class LiveTeamStatComparator implements Comparator<LiveTeamStat> {

    @Override
    public int compare(LiveTeamStat o1, LiveTeamStat o2) {
        int comparison = Integer.compare(o2.score.startingScore, o1.score.startingScore);
        if (comparison == 0) {
            comparison = Integer.compare(o2.score.subScore, o1.score.subScore);
        }
        return comparison;
    }
}
