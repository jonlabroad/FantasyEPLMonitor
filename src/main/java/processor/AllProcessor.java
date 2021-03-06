package processor;

import client.EPLClient;
import client.EPLClientFactory;
import config.CloudAppConfigProvider;
import config.GlobalConfig;
import config.PlayerProcessorConfig;
import data.EventInfo;
import data.ProcessedTeam;
import data.eplapi.*;
import dispatcher.MatchProcessorDispatcher;
import dispatcher.PlayerProcessorDispatcher;
import dispatcher.TeamProcessorDispatcher;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import persistance.S3JsonWriter;
import processor.league.LeagueProcessor;
import processor.team.EventProcessor;
import util.CloudConfigUpdater;

import java.util.*;

public class AllProcessor {

    private int _leagueId;
    private EPLClient _client = EPLClientFactory.createClient();

    public AllProcessor(int leagueId) {
        _leagueId = leagueId;
    }

    public String process(boolean isLambda) {
        // Force local lambdas
        GlobalConfig.LocalLambdas = true;

        if (!isLambda) {
            // Processing only performed if this is running on-premise
            HighlightProcessor highlightProcessor = new HighlightProcessor(GlobalConfig.CloudAppConfig.CurrentGameWeek, _leagueId);
            highlightProcessor.process();

            if (!isTimeToPoll()) {
                System.out.println("It's not time yet! Quiting...");
                return "No polling to do";
            }
        }

        DateTime start = DateTime.now();
        CloudConfigUpdater configUpdater = new CloudConfigUpdater(_client);
        boolean generateScoutingReports = true;
        if (configUpdater.update()) {
            generateScoutingReports = true;
        }

        EventProcessor eventProcessor = new EventProcessor(_client, GlobalConfig.CloudAppConfig.CurrentGameWeek);
        eventProcessor.process();

        PlayerProcessorConfig.getInstance().refresh(); // There appears to be caching going on (objs not unloaded from mem)
        HashMap<Integer, ProcessedTeam> processedTeams = new HashMap<>();
        try {
            PlayerProcessorDispatcher playerProcessor = new PlayerProcessorDispatcher(_client);
            playerProcessor.dispatchAll();

            Collection<Integer> teamsToProcess = _client.getTeamsInLeague(_leagueId);
            teamsToProcess.addAll(getAllCupOpponents(teamsToProcess));

            TeamProcessorDispatcher teamProcessor = new TeamProcessorDispatcher(_client, teamsToProcess, GlobalConfig.CloudAppConfig.CurrentGameWeek, _leagueId);
            teamProcessor.start();
            processedTeams = teamProcessor.join();

            estimateAverageScore(processedTeams);

            MatchProcessorDispatcher leagueMatchProcessor = new MatchProcessorDispatcher(_client, _leagueId, processedTeams,
                    _client.findMatches(_leagueId, GlobalConfig.CloudAppConfig.CurrentGameWeek));
            leagueMatchProcessor.dispatch();
            leagueMatchProcessor.join();

            MatchProcessorDispatcher cupMatchProcessor = new MatchProcessorDispatcher(_client, -1, processedTeams,
                    getAllCupMatches(processedTeams.keySet()));
            cupMatchProcessor.dispatch();
            cupMatchProcessor.join();

            if (generateScoutingReports) {
                generateScoutingReports(processedTeams);
            }

            try {
                AlertProcessor alertProcessor = new AlertProcessor(_leagueId, processedTeams.keySet(), _client);
                alertProcessor.process();
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            new LeagueProcessor(processedTeams.values(), _leagueId, GlobalConfig.CloudAppConfig.CurrentGameWeek).process();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        DateTime end = DateTime.now();
        System.out.format("Processing took %f sec\n", (end.getMillis() - start.getMillis()) / 1000.0);

        return null;
    }

    private void estimateAverageScore(HashMap<Integer, ProcessedTeam> teams)
    {
        ProcessedTeam average = teams.get(0);
        if (average != null && average.score.startingScore == 0) {
            int totalScore = 0;
            int numAvgd = 0;
            for (ProcessedTeam team : teams.values()) {
                if (team.id != 0) {
                    totalScore += team.score.startingScore + team.transferCost;
                    numAvgd++;
                }
            }
            int avgScore = totalScore/numAvgd;
            average.score.startingScore = avgScore;
        }
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

        if (GlobalConfig.CloudAppConfig.day == null || GlobalConfig.CloudAppConfig.day.isEmpty() || util.Date.fromString(GlobalConfig.CloudAppConfig.day).getDayOfMonth() != currentTime.getDayOfMonth()) {
            System.out.println("It's a new day!");
            GlobalConfig.CloudAppConfig.finalPollOfDayCompleted = false;
            GlobalConfig.CloudAppConfig.day = util.Date.toString(currentTime);
            new CloudAppConfigProvider().write(GlobalConfig.CloudAppConfig);
        }

        //if (!(currentTime.isAfter(eventStart) && (!event.finished || !event.data_checked))) {
        //    return false;
        //}

        if (!isFixtureTime(event)) {
            System.out.println("No fixtures are currently on");
            return false;
        }

        return true;
    }

    private void generateScoutingReports(HashMap<Integer, ProcessedTeam> teams) {
        try {
            ScoutingProcessor processor = new ScoutingProcessor(_leagueId, _client, teams);
            processor.process();
        }
        catch (Exception ex) {
            System.out.println("Scouting report generation failed. This is not fatal, but it still hurts :(");
            ex.printStackTrace();
        }
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

    private boolean isFixtureTime(Event event) {
        List<Fixture> todaysFixtures = getTodaysFixtures(event);

        if (false && allFixturesComplete(todaysFixtures)) {
            if (!GlobalConfig.CloudAppConfig.finalPollOfDayCompleted) {
                GlobalConfig.CloudAppConfig.finalPollOfDayCompleted = true;
                new CloudAppConfigProvider().write(GlobalConfig.CloudAppConfig);
                System.out.println("All fixtures for the day are complete! Performing final poll");
                return true;
            }
            System.out.println("All fixtures for the day are complete and final poll has been performed!");
            return false;
        }

        for(Fixture fixture : todaysFixtures) {
            System.out.format("%d (%d) @ (%d) %d: %s\n", fixture.team_a, fixture.team_a_score, fixture.team_h_score, fixture.team_h, fixture.kickoff_time);
            DateTime now = DateTime.now();
            System.out.println(now.toString());
            System.out.println(util.Date.fromApiString(fixture.kickoff_time).plusHours(7).withZone(DateTimeZone.forID("America/New_York")));
            if (fixture.started && now.isBefore(util.Date.fromApiString(fixture.kickoff_time).plusHours(7))) {
                System.out.format("Found fixture: %d @ %d\n", fixture.team_a, fixture.team_h);
                return true;
            }
        }
        return false;
    }

    private boolean allFixturesComplete(List<Fixture> fixtures) {
        boolean allComplete = fixtures.size() > 0 ? true : false;
        for (Fixture fixture : fixtures) {
            allComplete &= fixture.finished && fixture.finished_provisional;
        }
        return allComplete;
    }

    private List<Fixture> getTodaysFixtures(Event event) {
        Live liveData = _client.getLiveData(event.id);
        if (liveData == null) {
            return new ArrayList<>();
        }

        List<Fixture> retFixtures = new ArrayList<>();
        for(Fixture fixture : liveData.fixtures) {
            DateTime now = new DateTime();
            DateTime kickoff = util.Date.fromApiString(fixture.kickoff_time);

            if (kickoff.getDayOfMonth() == now.getDayOfMonth()) {
                retFixtures.add(fixture);
            }
        }
        return retFixtures;
    }

    private ArrayList<Match> getCups(int teamId) {
        EntryData entry = _client.getEntry(teamId);
        return (entry != null && entry.leagues.cup != null) ? entry.leagues.cup : new ArrayList<>();
    }

    private String formatDate(DateTime date) {
        return util.Date.toString(date);
    }

    private DateTime getEventStartTime(Event event) {
        return util.Date.fromApiString(event.deadline_time);
    }
}
