package processor;

import client.EPLClient;
import client.EPLClientFactory;
import config.GlobalConfig;
import config.PlayerProcessorConfig;
import data.ProcessedTeam;
import data.eplapi.BootstrapStatic;
import data.eplapi.EntryData;
import data.eplapi.Event;
import data.eplapi.Match;
import dispatcher.MatchProcessorDispatcher;
import dispatcher.PlayerProcessorDispatcher;
import dispatcher.TeamProcessorDispatcher;
import lambda.AllProcessorLambda;
import org.joda.time.DateTime;
import util.CloudConfigUpdater;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class AllProcessor {

    private int _leagueId;
    private EPLClient _client = EPLClientFactory.createClient();

    public AllProcessor(int leagueId) {
        _leagueId = leagueId;
    }

    public String process() {
        if (!isTimeToPoll()) {
            System.out.println("It's not time yet! Quiting...");
            return "No polling to do";
        }

        DateTime start = DateTime.now();
        CloudConfigUpdater configUpdater = new CloudConfigUpdater(_client);
        boolean generateScoutingReports = true;
        if (configUpdater.update()) {
            generateScoutingReports = true;
        }

        PlayerProcessorConfig.getInstance().refresh(); // There appears to be caching going on (objs not unloaded from mem)
        HashMap<Integer, ProcessedTeam> processedTeams = new HashMap<>();
        try {
            PlayerProcessorDispatcher playerProcessor = new PlayerProcessorDispatcher(_client);
            playerProcessor.dispatchAll();

            Collection<Integer> teamsToProcess = _client.getTeamsInLeague(_leagueId);
            teamsToProcess.addAll(getAllCupOpponents(teamsToProcess));

            TeamProcessorDispatcher teamProcessor = new TeamProcessorDispatcher(_client, teamsToProcess, GlobalConfig.CloudAppConfig.CurrentGameWeek);
            teamProcessor.start();
            processedTeams = teamProcessor.join();

            MatchProcessorDispatcher leagueMatchProcessor = new MatchProcessorDispatcher(_client, processedTeams,
                    _client.findMatches(_leagueId, GlobalConfig.CloudAppConfig.CurrentGameWeek));
            leagueMatchProcessor.dispatch();
            leagueMatchProcessor.join();

            MatchProcessorDispatcher cupMatchProcessor = new MatchProcessorDispatcher(_client, processedTeams,
                    getAllCupMatches(processedTeams.keySet()));
            cupMatchProcessor.dispatch();
            cupMatchProcessor.join();

        } catch (Exception e) {
            e.printStackTrace();
        }

        //if (generateScoutingReports) {
        //    generateScoutingReports();
        //}

        DateTime end = DateTime.now();
        System.out.format("Processing took %f sec\n", (end.getMillis() - start.getMillis()) / 1000.0);

        return null;
    }

    private ArrayList<Integer> getAllCupOpponents(Collection<Integer> teamIds) {
        ArrayList<Integer> cupTeamIds = new ArrayList<>();
        for (int teamId : teamIds) {
            Match cup = getCurrentCupMatch(teamId);
            if (cup != null) {
                cupTeamIds.add(teamId == cup.entry_1_entry ? cup.entry_2_entry : cup.entry_1_entry);
            }
        }
        return cupTeamIds;
    }

    private Collection<Match> getAllCupMatches(Collection<Integer> teamIds) {
        ArrayList<Match> matches = new ArrayList<>();
        for (int teamId : teamIds) {
            Match cup = getCurrentCupMatch(teamId);
            if (cup != null) {
                matches.add(cup);
            }
        }
        return matches;
    }

    private boolean isTimeToPoll() {
        if (GlobalConfig.TestMode) {
            return true;
        }

        DateTime currentTime = DateTime.now();
        Event event = getCurrentEvent();
        DateTime eventStart = getEventStartTime(event);
        System.out.format("Start date: %s\n", formatDate(eventStart));
        System.out.format("Current date: %s\n", formatDate(currentTime));
        System.out.format("Finished: %b\n", event.finished);
        System.out.format("Data checked: %b\n", event.data_checked);

        if (currentTime.isAfter(eventStart) && (!event.finished || !event.data_checked)) {
            return true;
        }
        return false;
    }

    private void generateScoutingReports() {
        Set<Future> futures = new HashSet<>();
        ExecutorService executor = Executors.newFixedThreadPool(10);

        for (Integer teamId : _client.getTeamsInLeague(_leagueId)) {
            //ScoutingProcessor processor = new ScoutingProcessor(_leagueId, teamId);
            //Runnable processRunnable = () -> processor.process();
            //futures.add(executor.submit(processRunnable));
        }

        for (Future future : futures) {
            try {
                System.out.format("Waiting for future...\n");
                future.get();
                System.out.println("Future complete");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        executor.shutdown();
    }

    private Event getCurrentEvent() {
        BootstrapStatic boot = _client.getBootstrapStatic();
        int currentEvent = boot.currentEvent;
        for (Event event : boot.events) {
            if (event.id == currentEvent) {
                return event;
            }
        }
        return null;
    }

    private Match getCurrentCupMatch(int teamId) {
        int gameweek = GlobalConfig.CloudAppConfig.CurrentGameWeek;
        ArrayList<Match> cups = getCups(teamId);
        for (Match cup : cups) {
            if (cup.event == gameweek) {
                return cup;
            }
        }
        return null;
    }

    private ArrayList<Match> getCups(int teamId) {
        EntryData entry = _client.getEntry(teamId);
        return entry.leagues.cup != null ? entry.leagues.cup : new ArrayList<>();
    }

    private String formatDate(DateTime date) {
        return util.Date.toString(date);
    }

    private DateTime getEventStartTime(Event event) {
        return util.Date.fromApiString(event.deadline_time);
    }

    public static void main(String[] args) {
        Map<String, Object> params = new HashMap<>();
        new AllProcessorLambda().handleRequest(params, null);
    }

}
