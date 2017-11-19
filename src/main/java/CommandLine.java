import config.*;
import persistance.S3JsonWriter;
import runner.GamedayRunner;
import com.mashape.unirest.http.exceptions.UnirestException;
import runner.PlaybackRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CommandLine {
    public static void main(String[] args) throws IOException, UnirestException, InterruptedException {
        //PregameRunner pregame = new PregameRunner();
        //pregame.run();

        //GlobalConfig.TestMode = true;
        //GlobalConfig.Record = false;
        //GamedayRunner runner = new GamedayRunner();
        //runner.run();

        List<Integer> teamIds = new ArrayList<>();
        teamIds.add(2365803);
        PlaybackRunner runner = new PlaybackRunner(teamIds);
        runner.run();

        //cleanRecordings();
    }

    private static void writeConfig() {
        System.out.println("WRITING TEST CONFIG. I HOPE YOU REALLY WANT THIS");
        String deviceId = "d2KCofjd1jQ"; // TODO update
        DeviceConfig config = new DeviceConfig(deviceId);
        config.addSubscription(1326527, "Pinky and De Bruyne");
        new DeviceConfigurator().writeConfig(config, deviceId);

        deviceId = "cjT6mHUW3O0"; // TODO update
        config = new DeviceConfig(deviceId);
        config.addSubscription(2365803, "The Vardy Boys");
        new DeviceConfigurator().writeConfig(config, deviceId);
    }

    private static void writeCloudAppConfig() {
        CloudAppConfig config = new CloudAppConfig();
        config.CurrentGameWeek = 10;
        new CloudAppConfigProvider().write(config);
    }

    private static void cleanRecordings() {
        S3JsonWriter writer = new S3JsonWriter();
        for (int i = 90; i < 100; i++) {
            String key = String.format("recorder/12/%d/responses", i);
            System.out.println(key);
            writer.delete(key);
        }
    }
}
