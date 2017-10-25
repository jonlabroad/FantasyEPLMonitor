import config.DeviceConfig;
import config.DeviceConfigurator;
import runner.GamedayRunner;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.io.IOException;

public class CommandLine {
    public static void main(String[] args) throws IOException, UnirestException, InterruptedException {
        //PregameRunner pregame = new PregameRunner();
        //pregame.run();

        GamedayRunner runner = new GamedayRunner();
        runner.run();
    }

    private static void writeConfig() {
        System.out.println("WRITING TEST CONFIG. I HOPE YOU REALLY WANT THIS");
        String deviceId = "d2KCofjd1jQ";
        DeviceConfig config = new DeviceConfig(deviceId);
        config.addSubscription(deviceId, 1326527, "Pinky and De Bruyne");
        new DeviceConfigurator().writeConfig(config, deviceId);

        deviceId = "cjT6mHUW3O0";
        config = new DeviceConfig(deviceId);
        config.addSubscription(deviceId, 2365803, "The Vardy Boys");
        new DeviceConfigurator().writeConfig(config, deviceId);
    }
}
