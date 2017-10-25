package alerts;

import data.*;

import java.util.List;

public class MatchEventGenerator {
    private int _teamId;
    private boolean _printOnly = true;

    public MatchEventGenerator(int teamId, boolean printOnly) {
        _teamId = teamId;
        _printOnly = printOnly;
    }

    public void Generate(MatchInfo newInfo, MatchInfo oldInfo) {
        List<MatchEvent> diff = new MatchInfoComparer(_teamId).Compare(oldInfo, newInfo);

        // TODO move this out of here!
        for (MatchEvent event : diff) {
            newInfo.matchEvents.add(event);
        }
    }

    public void GenerateScoutingReport(ScoutingReport report) {
        String alertText = report.toPregameString();
        // TODO reenable scouting report alert
        //SendAlert(alertText);
    }
}
