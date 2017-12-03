import config.*;
import dispatcher.GamedayDispatcher;
import dispatcher.PlayerProcessorDispatcher;
import lambda.AlertProcessorLambda;
import lambda.TeamProcessorLambda;
import org.joda.time.DateTime;
import persistance.S3JsonWriter;
import processor.PlayerProcessor;
import runner.GamedayRunner;
import com.mashape.unirest.http.exceptions.UnirestException;
import runner.PlaybackRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommandLine {
    public static void main(String[] args) throws IOException, UnirestException, InterruptedException {
        //PregameRunner pregame = new PregameRunner();
        //pregame.run();

        //GlobalConfig.TestMode = true;
        //GlobalConfig.Record = false;
        //GamedayRunner runner = new GamedayRunner();
        //runner.run();

        //List<Integer> teamIds = new ArrayList<>();
        //teamIds.add(2365803);
        //PlaybackRunner runner = new PlaybackRunner(teamIds);
        //runner.run();

        //GamedayDispatcher dispatcher = new GamedayDispatcher();
        //dispatcher.dispatch();

        //DateTime start = DateTime.now();
        //PlayerProcessor processor = new PlayerProcessor(1, 50);
        //processor.process();
        //DateTime stop = DateTime.now();
        //double diffSec = (stop.getMillis() - start.getMillis())/1000;
        //System.out.format("Player processing took %.1f seconds\n", diffSec);

        //writePlayerProcessorConfig();

        GlobalConfig.LocalLambdas = true;
        GlobalConfig.TestMode = true;
        //PlayerProcessorDispatcher dispatcher = new PlayerProcessorDispatcher();
        //dispatcher.dispatchAll();

        TeamProcessorLambda teamProcessor = new TeamProcessorLambda();
        teamProcessor.handleRequest(new HashMap<>(), null);

        //AlertProcessorLambda alertProcessorLambda = new AlertProcessorLambda();
        //alertProcessorLambda.handleRequest(new HashMap<>(), null);

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

    private static void writePlayerProcessorConfig() {
        PlayerProcessorConfig config = PlayerProcessorConfig.getInstance();
        config.record = true;
        config.recorderSequence = 0;
        config.write();
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
