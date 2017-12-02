package dispatcher;

import config.GlobalConfig;
import config.PlayerProcessorConfig;
import org.joda.time.DateTime;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class PlayerProcessorDispatcher {
    public void dispatchAll() {
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
    }

    private void setSequenceId() {
        PlayerProcessorConfig config = PlayerProcessorConfig.getInstance();
        config = config.refresh();
        config.recorderSequence++;
        config.write();
    }
}
