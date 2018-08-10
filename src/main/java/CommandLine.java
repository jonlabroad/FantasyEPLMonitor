import client.EPLClient;
import client.EPLClientFactory;
import client.Youtube.YoutubeClient;
import config.*;
import data.ProcessedLeagueFixtureList;
import data.eplapi.*;
import data.youtube.Item;
import lambda.AllProcessorLambda;
import org.joda.time.DateTime;
import persistance.S3JsonReader;
import persistance.S3JsonWriter;
import com.mashape.unirest.http.exceptions.UnirestException;
import processor.HighlightProcessor;
import processor.HybridAllProcessor;
import processor.scouting.H2hSimulator;
import processor.scouting.Record;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CommandLine {
    public static void main(String[] args) throws IOException, UnirestException, InterruptedException {
        //DateTime start = DateTime.now();
        //PlayerProcessor processor = new PlayerProcessor(1, 50);
        //processor.process();
        //DateTime stop = DateTime.now();
        //double diffSec = (stop.getMillis() - start.getMillis())/1000;
        //System.out.format("Player processing took %.1f seconds\n", diffSec);

        //writePlayerProcessorConfig();

        GlobalConfig.TestMode = false;

        //int leagueId = 5815;
        //processFixtures(leagueId);

        int leagueId = 5815;
        //new HighlightProcessor(GlobalConfig.CloudAppConfig.CurrentGameWeek, leagueId).process();

        HybridAllProcessor processor = new HybridAllProcessor(leagueId);
        processor.process();

        //Map<String, Object> params = new HashMap<>();
        //int leagueId = getLeagueId(args);
        //params.put("leagueid", leagueId > -1 ? leagueId : 5815);
        //AllProcessorLambda allProcessor = new AllProcessorLambda(false);
        //allProcessor.handleRequest(params, null);

        //calculateUltimateH2h();

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

    private static int getLeagueId(String args[])
    {
        if (args.length < 1) {
            return -1;
        }
        return Integer.parseInt(args[0]);
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

    private static void processFixtures(int leagueId) {
        ProcessedLeagueFixtureList processed = new ProcessedLeagueFixtureList();
        S3JsonReader reader = new S3JsonReader();
        for (int i = 1; i <= 38; i++) {
            LeagueEntriesAndMatches matches = reader.read(String.format(GlobalConfig.DataRoot + "/%d/fixtures/leagues-entries-and-h2h-matches-%d-page-%d.json", leagueId, leagueId, i), LeagueEntriesAndMatches.class);
            processed.league = matches.league;
            for (Match match : matches.matches.results) {
                if (!processed.matches.containsKey(match.event)) {
                    processed.matches.put(match.event, new ArrayList<>());
                }
                processed.matches.get(match.event).add(match);
            }
        }
        S3JsonWriter writer = new S3JsonWriter();
        writer.write(String.format(GlobalConfig.DataRoot + "/%d/fixtures/fixtures.json", leagueId), processed);
    }

    private static void calculateUltimateH2h() {
        EPLClient client = EPLClientFactory.createClient();
        Standings standings = client.getStandings(31187);
        HashMap<Integer, Record> records = new HashMap<>();
        for (Standing standing : standings.standings.results) {
            Record newRecord = new Record();
            newRecord.teamName = standing.entry_name;
            newRecord.teamId = standing.entry;
            records.put(standing.entry, newRecord);
        }

        for (Standing standing : standings.standings.results) {
            for (Standing opp : standings.standings.results) {
                if (standing.entry == opp.entry) {
                    continue;
                }

                H2hSimulator simulator = new H2hSimulator(client, standing.entry, opp.entry);
                HashMap<Integer, Record> rec = simulator.simulate();
                for (int teamId : rec.keySet()) {
                    records.get(teamId).wins += rec.get(teamId).wins;
                    records.get(teamId).draws += rec.get(teamId).draws;
                    records.get(teamId).losses += rec.get(teamId).losses;
                }
            }
        }

        ArrayList<Record> recList = new ArrayList<>(records.values());
        Collections.sort(recList);
        int place = 1;
        for (Record rec : recList) {
            Standing standing = null;
            for(Standing s : standings.standings.results) {
                if (s.entry == rec.teamId) {
                    standing = s;
                    break;
                }
            }

            System.out.format("%d. %s: %dW-%dD-%dL %d %d\n", place, rec.teamName, rec.wins, rec.draws, rec.losses, rec.getPoints(), place - standing.rank);
            place++;
        }
    }
}
