package runner;

import config.GlobalConfig;
import persistance.S3MatchInfoDatastore;

import java.util.ArrayList;
import java.util.Collection;

public class PlaybackRunner extends CommonRunner {
    private Collection<Integer> _teamIds = new ArrayList<>();

    public PlaybackRunner() {
        super();
    }

    public PlaybackRunner(Collection<Integer> teamIds) {
        _teamIds.addAll(teamIds);
    }

    public void runImpl(int teamId) {
        setPlaybackParameters();
        clearMatchInfo();
        runPlayback(1, 19);
    }

    @Override
    public void run() {
        for (Integer teamId : _teamIds) {
            runImpl(teamId);
        }
    }

    public void setPlaybackParameters() {
        GlobalConfig.TestMode = true;
        GlobalConfig.PlaybackMode = true;
        GlobalConfig.PlaybackGameweek = 12;
        GlobalConfig.CurrentPlaybackSequence = 0;
        GlobalConfig.Record = false;
        GlobalConfig.MatchInfoRoot = "testdata";
    }

    private static void runPlayback(int startSequence, int stopSequence) {
        for (int s = startSequence; s <= stopSequence; s++) {
            System.out.format("Sequence: %d:\n", s);
            try {
                GlobalConfig.CurrentPlaybackSequence = s;
                GamedayRunner runner = new GamedayRunner();
                runner.run();
            }
            catch (Exception ex) {
                System.out.println(ex.toString());
            }
        }
    }

    private void clearMatchInfo() {
        for (Integer teamId : _teamIds) {
            new S3MatchInfoDatastore(_leagueId).delete(teamId, GlobalConfig.PlaybackGameweek);
        }
    }
}
