package dispatcher;

import client.EPLClient;
import client.EPLClientFactory;
import config.CloudAppConfigProvider;
import config.GlobalConfig;
import config.PlayerProcessorConfig;
import data.eplapi.BootstrapStatic;
import data.eplapi.Event;
import lambda.AwsLambdaInvoker;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class PlayerProcessorDispatcher {
    EPLClient _client;

    public PlayerProcessorDispatcher() {
        _client = EPLClientFactory.createClient();
    }

    public void dispatchAll() {
        if (!isTimeToPoll()) {
            System.out.println("It's not time yet! Quiting...");
            return;
        }

        Event currentEvent = getCurrentEvent();
        if (currentEvent.id != GlobalConfig.CloudAppConfig.CurrentGameWeek) {
            GlobalConfig.CloudAppConfig.CurrentGameWeek = currentEvent.id;
            new CloudAppConfigProvider().write(GlobalConfig.CloudAppConfig);
        }

        Set<Future> futures = new HashSet<>();
        ExecutorService executor = Executors.newFixedThreadPool(10);

        setSequenceId();

        DateTime start = DateTime.now();
        for (int i = 1; i < 600; i += GlobalConfig.NumberFootballersToProcessPerLambda) {
            SinglePlayerProcessorDispatcher dispatcher = new SinglePlayerProcessorDispatcher(i,
                    i + GlobalConfig.NumberFootballersToProcessPerLambda - 1,
                    GlobalConfig.LocalLambdas);
            Runnable dispatchRunnable = () -> dispatcher.dispatch();
            futures.add(executor.submit(dispatchRunnable));
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
        DateTime end = DateTime.now();
        System.out.format("Player processing took %f sec\n", (end.getMillis() - start.getMillis())/1000.0);

        System.out.println("Dispatching TeamProcessor (async)");
        AwsLambdaInvoker invoker = new AwsLambdaInvoker();
        invoker.invoke("EPLFantasyTeamProcessor", new HashMap<>(), true);
        System.out.println("Done");
        executor.shutdown();
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
}
