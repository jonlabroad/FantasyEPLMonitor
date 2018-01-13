import client.EPLClient;
import client.EPLClientFactory;
import config.*;
import data.ProcessedLeagueFixtureList;
import data.eplapi.FootballerScoreDetailElement;
import data.eplapi.LeagueEntriesAndMatches;
import data.eplapi.Live;
import data.eplapi.Match;
import lambda.AllProcessorLambda;
import persistance.S3JsonReader;
import persistance.S3JsonWriter;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

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

        //PlayerProcessor processor = new PlayerProcessor();
        //processor.process();

        //TeamProcessorLambda teamProcessor = new TeamProcessorLambda();
        //teamProcessor.handleRequest(new HashMap<>(), null);

        //ScoutingProcessor processor = new ScoutingProcessor(31187, 2365803);
        //processor.process();

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
        FootballerScoreDetailElement explains = data.elements.get(245).getExplains().get(0);
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
        config.CurrentGameWeek = 23;
        config.finalPollOfDayCompleted = false;
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

    private static void processFixtures() {
        ProcessedLeagueFixtureList processed = new ProcessedLeagueFixtureList();
        S3JsonReader reader = new S3JsonReader();
        for (int i = 1; i <= 13; i++) {
            LeagueEntriesAndMatches matches = reader.read(String.format("data/31187/fixtures/leagues-entries-and-h2h-matches-31187-page-%d.json", i), LeagueEntriesAndMatches.class);
            processed.league = matches.league;
            for (Match match : matches.matches.results) {
                if (!processed.matches.containsKey(match.event)) {
                    processed.matches.put(match.event, new ArrayList<>());
                }
                processed.matches.get(match.event).add(match);
            }
        }
        S3JsonWriter writer = new S3JsonWriter();
        writer.write("data/31187/fixtures/fixtures.json", processed);
    }
}
