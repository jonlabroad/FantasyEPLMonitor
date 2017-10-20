package Runner;

import Alerts.AlertGenerator;
import Alerts.ScoutingReport;
import Persistance.S3MatchInfoDatastore;

import java.util.ArrayList;

public class PregameRunner extends CommonRunner {
    private boolean _force = true;
    private boolean _printOnly = false;

    public PregameRunner() {
        super();
    }

    public PregameRunner(ArrayList<Integer> teamIds) {
        super(teamIds);
    }

    public void RunImpl(int teamId) {
        if (_force || (_prevNextMatchInfo == null && _nextMatchInfo != null)) {
            ScoutingReport report = new ScoutingReport(_nextMatchInfo, true);
            new S3MatchInfoDatastore(teamId).WriteNext(_nextMatchInfo);
            AlertGenerator alertGen = new AlertGenerator(teamId, _printOnly);
            alertGen.GenerateScoutingReport(report);
        }
        //System.out.println(report.toPregameString());
    }
}

