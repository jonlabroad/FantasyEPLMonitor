import Config.DeviceConfig;
import Config.DeviceConfigurator;
import Runner.GamedayRunner;
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
        /*
        DeviceConfig config = new DeviceConfig();
        config.addSubscription("d2KCofjd1jQ", 1326527, "Pinky and De Bruyne");
        config.addSubscription("cjT6mHUW3O0", 2365803, "The Vardy Boys");
        new DeviceConfigurator().writeConfig(config);
        */
    }
}
