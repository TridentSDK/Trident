package net.tridentsdk.server.unit;

import org.junit.Assert;
import org.junit.runners.model.RunnerScheduler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AbstractTest extends Assert implements RunnerScheduler {
    private final ExecutorService service = Executors.newCachedThreadPool();

    @Override
    public void schedule(Runnable runnable) {
        service.execute(runnable);
    }

    @Override
    public void finished() {
        service.shutdownNow();
    }
}
