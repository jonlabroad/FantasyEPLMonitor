package lambda;

import client.EPLClient;
import client.EPLClientFactory;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.mashape.unirest.http.exceptions.UnirestException;
import config.CloudAppConfigProvider;
import config.GlobalConfig;
import config.PlayerProcessorConfig;
import data.eplapi.BootstrapStatic;
import data.eplapi.Event;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import processor.CupProcessor;
import processor.PlayerProcessor;
import processor.TeamProcessor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AllProcessorLambda implements RequestHandler<Map<String, Object>, String> {
    EPLClient _client =EPLClientFactory.createClient();

    public String handleRequest(Map<String, Object> params, Context context) {
        if (!isTimeToPoll()) {
            System.out.println("It's not time yet! Quiting...");
            return "No polling to do";
        }

        DateTime start = DateTime.now();
        Event currentEvent = getCurrentEvent();
        if (currentEvent.id != GlobalConfig.CloudAppConfig.CurrentGameWeek) {
            GlobalConfig.CloudAppConfig.CurrentGameWeek = currentEvent.id;
            new CloudAppConfigProvider().write(GlobalConfig.CloudAppConfig);
        }

        ILambdaInvoker invoker = new LocalAwsLambdaInvoker();
        PlayerProcessorConfig.getInstance().refresh(); // There appears to be caching going on (objs not unloaded from mem)
        try {
            invoker.invoke("EPLFantasyPlayerProcessor", new HashMap<>(), false);
            invoker.invoke("EPLFantasyTeamProcessor", new HashMap<>(), false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        CupProcessor processor = new CupProcessor(new ArrayList<>(), 31187, false);
        processor.process();

        DateTime end = DateTime.now();
        System.out.format("Processing took %f sec\n", (end.getMillis() - start.getMillis())/1000.0);

        return null;
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

    private String formatDate(DateTime date) {
        return date.toString(getDateFormatter());
    }

    private DateTime getEventStartTime(Event event) {
        DateTimeFormatter fmt = getDateFormatter();
        return fmt.parseDateTime(event.deadline_time);
    }

    private DateTimeFormatter getDateFormatter() {
        return DateTimeFormat.forPattern("YYYY-MM-dd'T'HH:mm:ss'Z'").withZone(DateTimeZone.forID("Europe/London"));
    }

    private void setSequenceId() {
        PlayerProcessorConfig config = PlayerProcessorConfig.getInstance();
        config = config.refresh();
        config.recorderSequence++;
        config.write();
    }

    public static void main(String[] args) {
        Map<String, Object> params = new HashMap<>();
        new AllProcessorLambda().handleRequest(params, null);
    }
}
