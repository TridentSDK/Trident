package net.tridentsdk.server;

import net.tridentsdk.api.reflect.FastClass;
import net.tridentsdk.api.scheduling.Scheduler;
import net.tridentsdk.api.scheduling.TridentRunnable;
import net.tridentsdk.plugin.TridentPlugin;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class TridentScheduler implements Scheduler {

    private int currentId = 0;

    private ConcurrentHashMap<RunnableWrapper, AtomicLong> asyncTasks;

    private ConcurrentHashMap<RunnableWrapper, AtomicLong> syncTasks;

    // basically a thread pool that has an entry for a thread, and the number of plugins assigned to it
    private ConcurrentHashMap<ExecutorService, AtomicInteger> threads;

    private ConcurrentHashMap<TridentPlugin, ExecutorService> threadAssignments;

    private CopyOnWriteArraySet<Integer> cancelledId;

    public TridentScheduler() {
        asyncTasks = new ConcurrentHashMap<>();
        threads = new ConcurrentHashMap<>();
        threadAssignments = new ConcurrentHashMap<>();
        syncTasks = new ConcurrentHashMap<>();
        cancelledId = new CopyOnWriteArraySet<>();
        // use two for now, can be more later
        threads.put(Executors.newSingleThreadExecutor(), new AtomicInteger(0));
        threads.put(Executors.newSingleThreadExecutor(), new AtomicInteger(0));
    }

    private ExecutorService getLeastUsed() {
        ExecutorService retVal = null;
        int used = -1;
        for (Map.Entry<ExecutorService, AtomicInteger> entry : threads.entrySet()) {
            if (entry.getValue().get() > used) {
                retVal = entry.getKey();
                used = entry.getValue().get();
            }
        }
        return retVal;
    }

    private void addUse(ExecutorService service) {
        AtomicInteger integer = threads.get(service);
        integer.getAndIncrement();
        threads.put(service, integer);
    }

    private ExecutorService getCachedAssignment(TridentPlugin plugin) {
        if (threadAssignments.containsKey(plugin)) {
            return threadAssignments.get(plugin);
        } else {
            ExecutorService retVal = getLeastUsed();
            addUse(retVal);
            threadAssignments.put(plugin, retVal);
            return retVal;
        }
    }

    @Override
    public synchronized TridentRunnable runTaskAsynchronously(TridentPlugin plugin, TridentRunnable runnable) {
        assignId(runnable);
        threadAssignments.get(plugin).submit(runnable);
        return runnable;
    }

    @Override
    public synchronized TridentRunnable runTaskSynchronously(TridentPlugin plugin, TridentRunnable runnable) {
        assignId(runnable);
        syncTasks.put(new RunnableWrapper(runnable, plugin), new AtomicLong(0L));
        return runnable;
    }

    @Override
    public synchronized TridentRunnable runTaskAsyncLater(TridentPlugin plugin, TridentRunnable runnable, long delay) {
        assignId(runnable);
        asyncTasks.put(new RunnableWrapper(runnable, plugin), new AtomicLong(delay));
        return runnable;
    }

    @Override
    public synchronized TridentRunnable runTaskSyncLater(TridentPlugin plugin, TridentRunnable runnable, long delay) {
        assignId(runnable);
        syncTasks.put(new RunnableWrapper(runnable, plugin), new AtomicLong(delay));
        return runnable;
    }

    @Override
    public synchronized TridentRunnable runTaskAsyncRepeating(TridentPlugin plugin, TridentRunnable runnable,
                                                              long delay, long initialInterval) {
        assignId(runnable);
        syncTasks.put(new RunnableWrapper(runnable, plugin, true), new AtomicLong(initialInterval));
        return null;
    }

    @Override
    public synchronized TridentRunnable runTaskSyncRepeating(TridentPlugin plugin, TridentRunnable runnable,
                                                             long delay, long initialInterval) {
        assignId(runnable);
        asyncTasks.put(new RunnableWrapper(runnable, plugin, true), new AtomicLong(initialInterval));
        return null;
    }

    @Override
    public void cancel(int id) {
        this.cancelledId.add(id);
    }

    @Override
    public void cancel(TridentRunnable runnable) {
        this.cancelledId.add(runnable.getId());
    }

    /**
     * Called when the server ticks, the lifeblood of this class, should only be called on the main thread unless
     * you want difficulties
     */
    public void tick() {
        for (Map.Entry<RunnableWrapper, AtomicLong> entry : asyncTasks.entrySet()) {
            if(cancelledId.contains(entry.getKey().getRunnable().getId())) {
                asyncTasks.remove(entry.getKey());
                continue;
            }
            long time = entry.getValue().decrementAndGet();
            if (time <= 0) {
                RunnableWrapper wrapper = entry.getKey();
                threadAssignments.get(wrapper.getPlugin()).submit(wrapper.getRunnable());
                asyncTasks.remove(wrapper);
                if(wrapper.isRepeating()) {
                    asyncTasks.put(wrapper, new AtomicLong(wrapper.getRunnable().getInterval()));
                }
            }
        }

        for (Map.Entry<RunnableWrapper, AtomicLong> entry : syncTasks.entrySet()) {
            if(cancelledId.contains(entry.getKey().getRunnable().getId())) {
                syncTasks.remove(entry.getKey());
                continue;
            }
            long time = entry.getValue().decrementAndGet();
            if (time <= 0) {
                RunnableWrapper wrapper = entry.getKey();
                wrapper.getRunnable().run();
                syncTasks.remove(wrapper);
                if(wrapper.isRepeating()) {
                    syncTasks.put(wrapper, new AtomicLong(wrapper.getRunnable().getInterval()));
                }
            }
        }

    }

    /**
     * Uses fast reflection to assign an ID to each runnable, that way there is no publicly exposed "setId()" that
     * could break things
     *
     * @param runnable
     */
    private void assignId(TridentRunnable runnable) {
        FastClass.get(TridentRunnable.class).getField(runnable, "id").set(new Integer(currentId));
        currentId++;
    }

    private class RunnableWrapper {
        private final TridentRunnable runnable;
        private final TridentPlugin plugin;
        private final boolean repeating;

        private RunnableWrapper(TridentRunnable runnable, TridentPlugin plugin) {
            this(runnable, plugin, false);
        }

        private RunnableWrapper(TridentRunnable runnable, TridentPlugin plugin, boolean repeating) {
            this.plugin = plugin;
            this.runnable = runnable;
            this.repeating = repeating;
        }

        public boolean isRepeating() {
            return repeating;
        }

        public TridentRunnable getRunnable() {
            return runnable;
        }

        public TridentPlugin getPlugin() {
            return plugin;
        }
    }
}
