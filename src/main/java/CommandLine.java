import client.EPLClient;
import client.EPLClientFactory;
import com.google.gson.Gson;
import config.*;
import data.ProcessedPlayerCollection;
import data.eplapi.FootballerScoreDetailElement;
import data.eplapi.Live;
import dispatcher.PlayerProcessorDispatcher;
import lambda.AllProcessorLambda;
import lambda.TeamProcessorLambda;
import org.apache.commons.io.Charsets;
import persistance.S3JsonWriter;
import com.mashape.unirest.http.exceptions.UnirestException;
import processor.CupProcessor;
import processor.PlayerProcessor;
import processor.player.SinglePlayerProcessor;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommandLine {
    public static void main(String[] args) throws IOException, UnirestException, InterruptedException {
        //DateTime start = DateTime.now();
        //PlayerProcessor processor = new PlayerProcessor(1, 50);
        //processor.process();
        //DateTime stop = DateTime.now();
        //double diffSec = (stop.getMillis() - start.getMillis())/1000;
        //System.out.format("Player processing took %.1f seconds\n", diffSec);

        //writePlayerProcessorConfig();

        GlobalConfig.LocalLambdas = true;
        GlobalConfig.TestMode = false;

        AllProcessorLambda allProcessor = new AllProcessorLambda();
        allProcessor.handleRequest(new HashMap<>(), null);

        //PlayerProcessorDispatcher dispatcher = new PlayerProcessorDispatcher();
        //dispatcher.dispatchAll();

        //TeamProcessorLambda teamProcessor = new TeamProcessorLambda();
        //teamProcessor.handleRequest(new HashMap<>(), null);

        //AlertProcessorLambda alertProcessorLambda = new AlertProcessorLambda();
        //alertProcessorLambda.handleRequest(new HashMap<>(), null);

        //PlayerProcessor processor = new PlayerProcessor();
        //processor.process();

        //CupProcessor processor = new CupProcessor(new ArrayList<>(), 31187, false);
        //processor.process();

        //cleanRecordings();
    }

    private static Live readLiveData() {
        EPLClient client = EPLClientFactory.createHttpClient();
        Live data = client.readLiveEventData(20);
        FootballerScoreDetailElement explains = data.elements.get(245).getExplain();
        return data;
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
