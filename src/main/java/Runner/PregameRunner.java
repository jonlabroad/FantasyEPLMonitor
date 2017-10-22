package Runner;

import Alerts.AlertGenerator;
import Alerts.ScoutingReport;
import Data.MatchInfo;
import Persistance.S3MatchInfoDatastore;

public class PregameRunner extends CommonRunner {
    private boolean _force = false;
    private boolean _printOnly = false;

    protected MatchInfo _nextMatchInfo = null;
    protected MatchInfo _prevNextMatchInfo = null;

    public PregameRunner() {
        super();
    }

    public void runImpl(int teamId) {
        if (_force || (_prevNextMatchInfo == null && _nextMatchInfo != null)) {
            ScoutingReport report = new ScoutingReport(_nextMatchInfo, true);
            new S3MatchInfoDatastore().writeNext(teamId, _nextMatchInfo);
            AlertGenerator alertGen = new AlertGenerator(teamId, _printOnly);
            alertGen.GenerateScoutingReport(report);
        }
    }

    protected void PreRunTasks() {
        for (int teamId : _teamIds) {
            try {
                _nextMatchInfo = _client.GetMatchInfo(_leagueId, teamId, true);
                if (!_forceUpdate) {
                    _prevNextMatchInfo = _matchInfoDatastore.readNextMatchInfo(teamId, _nextMatchInfo.match.event);
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
}

