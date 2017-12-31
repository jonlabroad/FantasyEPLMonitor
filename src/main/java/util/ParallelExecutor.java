package util;

import config.GlobalConfig;
import dispatcher.SinglePlayerProcessorDispatcher;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ParallelExecutor {

    ArrayList<IParallelizableProcess> _processes;
    Set<Future> _futures;
    ExecutorService _executor;

    public ParallelExecutor() {
        _processes = new ArrayList<>();
        _futures = new HashSet<>();
        _executor = Executors.newFixedThreadPool(10);
    }

    public void add(IParallelizableProcess process) {
        _processes.add(process);
    }

    public void start() {
        for (IParallelizableProcess process : _processes) {
            Runnable processor = () -> process.process();
            _futures.add(_executor.submit(processor));
        }
    }

    public void join() {
        for (Future future : _futures) {
            try {
                future.get();
                System.out.println("Future complete");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    public void close() {
        _executor.shutdown();
    }
}
