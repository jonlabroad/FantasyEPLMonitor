package runner;

import data.LegacyMatchInfo;

public class PregameRunner extends CommonRunner {
    private boolean _force = false;
    private boolean _printOnly = false;

    protected LegacyMatchInfo _nextLegacyMatchInfo = null;
    protected LegacyMatchInfo _prevNextLegacyMatchInfo = null;

    public PregameRunner() {
        super();
    }

    public void runImpl(int teamId) {
        /*
        if (_force || (_prevNextLegacyMatchInfo == null && _nextLegacyMatchInfo != null)) {
            ScoutingReport report = new ScoutingReport(_nextLegacyMatchInfo, true);
            new S3MatchInfoDatastore(_leagueId).writeNext(teamId, _nextLegacyMatchInfo);
            MatchEventGenerator alertGen = new MatchEventGenerator(teamId, _printOnly);
            alertGen.GenerateScoutingReport(report);
        }
        */
    }

    protected void PreRunTasks() {
        for (int teamId : _teamIds) {
            try {
                _nextLegacyMatchInfo = _client.getMatchInfo(_leagueId, teamId, true);
                if (!_forceUpdate) {
                    _prevNextLegacyMatchInfo = _matchInfoDatastore.readNextMatchInfo(teamId, _nextLegacyMatchInfo.match.event);
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
}

