import config.*;
import runner.GamedayRunner;
import com.mashape.unirest.http.exceptions.UnirestException;
import runner.PlaybackRunner;

import java.io.IOException;

public class CommandLine {
    public static void main(String[] args) throws IOException, UnirestException, InterruptedException {
        //PregameRunner pregame = new PregameRunner();
        //pregame.run();

        GamedayRunner runner = new GamedayRunner();
        runner.run();

        //PlaybackRunner runner = new PlaybackRunner();
        //runner.run();
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
}
