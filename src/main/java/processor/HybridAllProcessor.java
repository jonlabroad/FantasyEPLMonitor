package processor;

import client.EPLClient;
import client.EPLClientFactory;
import config.CloudAppConfigProvider;
import config.GlobalConfig;
import data.eplapi.*;
import dispatcher.AllProcessorDispatcher;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.ArrayList;
import java.util.List;

public class HybridAllProcessor {

    private int _leagueId;
    private EPLClient _client = EPLClientFactory.createClient();

    public HybridAllProcessor(int leagueId) {
        _leagueId = leagueId;
    }

    public String process() {
        // Processing only performed if this is running on-premise
        HighlightProcessor highlightProcessor = new HighlightProcessor(GlobalConfig.CloudAppConfig.CurrentGameWeek, _leagueId);
        highlightProcessor.process();

        if (false && !isTimeToPoll()) {
            System.out.println("It's not time yet! Quiting...");
            return "No polling to do";
        }

        System.out.println("Running lambda...");
        AllProcessorDispatcher dispatcher = new AllProcessorDispatcher(true);
        dispatcher.process();
        System.out.println("Done");

        return "Done";
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

        if (GlobalConfig.CloudAppConfig.day == null || util.Date.fromString(GlobalConfig.CloudAppConfig.day).getDayOfMonth() != currentTime.getDayOfMonth()) {
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
