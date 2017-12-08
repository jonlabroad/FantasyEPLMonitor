package client;

import client.Request.PlaybackRequestExecutor;
import client.Request.RequestExecutor;
import config.GlobalConfig;

import java.io.IOException;

public class EPLClientFactory {
    public static EPLClient createClient() {
        if (GlobalConfig.PlaybackMode) {
            return createPlaybackClient();
        }
        return createHttpClient();
    }

    public static EPLClient createHttpClient() {
        try {
            return new EPLClient(new RequestExecutor(false, 0));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static EPLClient createHttpClient(boolean record, int currentSequence) {
        try {
            return new EPLClient(new RequestExecutor(record, currentSequence));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static EPLClient createPlaybackClient() {
        try {
            return new EPLClient(new PlaybackRequestExecutor(GlobalConfig.PlaybackGameweek));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
