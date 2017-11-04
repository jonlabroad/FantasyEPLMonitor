package runner;

import config.GlobalConfig;

public class PlaybackRunner extends CommonRunner {
    public PlaybackRunner() {
        super();
    }

    public void runImpl(int teamId) {

        SetPlaybackParameters();
        runPlayback(0, 6);
    }

    @Override
    public void run() {
        runImpl(1);
    }

    public void SetPlaybackParameters() {
        GlobalConfig.TestMode = true;
        GlobalConfig.PlaybackMode = true;
        GlobalConfig.PlaybackGameweek = 11;
        GlobalConfig.CurrentPlaybackSequence = 0;
        GlobalConfig.Record = false;
    }

    private static void runPlayback(int startSequence, int stopSequence) {
        for (int s = startSequence; s <= stopSequence; s++) {
            System.out.format("Sequence: %d:\n", s);
            GlobalConfig.CurrentPlaybackSequence = s;
            GamedayRunner runner = new GamedayRunner();
            runner.run();
        }
    }
}
