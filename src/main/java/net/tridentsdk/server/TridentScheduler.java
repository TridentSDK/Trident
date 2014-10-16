/*
 *     Trident - A Multithreaded Server Alternative
 *     Copyright (C) 2014, The TridentSDK Team
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.tridentsdk.server;

import net.tridentsdk.api.Trident;
import net.tridentsdk.api.reflect.FastClass;
import net.tridentsdk.api.scheduling.Scheduler;
import net.tridentsdk.api.scheduling.TridentRunnable;
import net.tridentsdk.plugin.TridentPlugin;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class TridentScheduler implements Scheduler {

    private final Map<RunnableWrapper, AtomicLong> asyncTasks = new ConcurrentHashMap<>();
    private final Map<RunnableWrapper, AtomicLong> syncTasks = new ConcurrentHashMap<>();
    // basically a thread pool that has an entry for a thread, and the number of plugins assigned to it
    private final Map<ExecutorService, AtomicInteger> threads = new ConcurrentHashMap<>();
    private final Map<TridentPlugin, ExecutorService> threadAssignments = new ConcurrentHashMap<>();
    private final Collection<Integer> cancelledId = new CopyOnWriteArraySet<>();
    private final Map<Future<?>, RunnableWrapper> asyncReturns = new ConcurrentHashMap<>();
    private final Collection<Map.Entry<Future<?>, String>> syncReturns = new HashSet<>();
    private int currentId;

    public TridentScheduler() {
        // use two for now, can be more later
        this.threads.put(Executors.newSingleThreadExecutor(), new AtomicInteger(0));
        this.threads.put(Executors.newSingleThreadExecutor(), new AtomicInteger(0));
    }

    private ExecutorService getMostUsed() {
        ExecutorService retVal = null;
        int used = -1;

        for (Map.Entry<ExecutorService, AtomicInteger> entry : this.threads.entrySet()) {
            if (entry.getValue().get() > used) {
                retVal = entry.getKey();
                used = entry.getValue().get();
            }
        }

        return retVal;
    }

    private void addUse(ExecutorService service) {
        this.threads.get(service).incrementAndGet();
    }

    private ExecutorService getCachedAssignment(TridentPlugin plugin) {
        if (this.threadAssignments.containsKey(plugin)) {
            return this.threadAssignments.get(plugin);
        } else {
            ExecutorService retVal = this.getMostUsed();

            this.addUse(retVal);
            this.threadAssignments.put(plugin, retVal);

            return retVal;
        }
    }

    @Override
    public TridentRunnable runTaskAsynchronously(TridentPlugin plugin, TridentRunnable runnable) {
        synchronized (this) {
            this.assignId(runnable);
            this.executeAsync(new RunnableWrapper(runnable, plugin));

            return runnable;
        }
    }

    @Override
    public TridentRunnable runTaskSynchronously(TridentPlugin plugin, TridentRunnable runnable) {
        synchronized (this) {
            this.assignId(runnable);
            this.syncTasks.put(new RunnableWrapper(runnable, plugin), new AtomicLong(0L));

            return runnable;
        }
    }

    @Override
    public TridentRunnable runTaskAsyncLater(TridentPlugin plugin, TridentRunnable runnable, long delay) {
        synchronized (this) {
            this.assignId(runnable);
            this.asyncTasks.put(new RunnableWrapper(runnable, plugin), new AtomicLong(delay));

            return runnable;
        }
    }

    @Override
    public TridentRunnable runTaskSyncLater(TridentPlugin plugin, TridentRunnable runnable, long delay) {
        synchronized (this) {
            this.assignId(runnable);
            this.syncTasks.put(new RunnableWrapper(runnable, plugin), new AtomicLong(delay));

            return runnable;
        }
    }

    @Override
    public TridentRunnable runTaskAsyncRepeating(TridentPlugin plugin, TridentRunnable runnable,
            long delay, long initialInterval) {
        synchronized (this) {
            this.assignId(runnable);
            this.syncTasks.put(new RunnableWrapper(runnable, plugin, true), new AtomicLong(initialInterval));

            return null;
        }
    }

    @Override
    public TridentRunnable runTaskSyncRepeating(TridentPlugin plugin, TridentRunnable runnable,
            long delay, long initialInterval) {
        synchronized (this) {
            this.assignId(runnable);
            this.asyncTasks.put(new RunnableWrapper(runnable, plugin, true), new AtomicLong(initialInterval));

            return null;
        }
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
     * Called when the server ticks, the lifeblood of this class, should only be called on the main thread unless you
     * want difficulties
     */
    public void tick() {
        for (Map.Entry<Future<?>, RunnableWrapper> entry : this.asyncReturns.entrySet()) {
            if (entry.getKey().isDone()) {
                entry.getValue().getRunnable().runAfterSync();
                this.asyncReturns.remove(entry.getKey());
            }
        }

        for (Map.Entry<RunnableWrapper, AtomicLong> entry : this.asyncTasks.entrySet()) {
            if (this.cancelledId.contains(entry.getKey().getRunnable().getId())) {
                this.asyncTasks.remove(entry.getKey());
                continue;
            }

            long time = entry.getValue().decrementAndGet();

            if (time <= 0L) {
                RunnableWrapper wrapper = entry.getKey();

                this.executeAsync(wrapper);
                this.asyncTasks.remove(wrapper);

                if (wrapper.isRepeating()) {
                    this.asyncTasks.put(wrapper, new AtomicLong(wrapper.getRunnable().getInterval()));
                }
            }
        }

        for (Map.Entry<RunnableWrapper, AtomicLong> entry : this.syncTasks.entrySet()) {
            if (this.cancelledId.contains(entry.getKey().getRunnable().getId())) {
                this.syncTasks.remove(entry.getKey());
                continue;
            }

            long time = entry.getValue().decrementAndGet();

            if (time <= 0L) {
                RunnableWrapper wrapper = entry.getKey();

                this.executeSync(wrapper, false);
                this.syncTasks.remove(wrapper);

                if (wrapper.isRepeating()) {
                    this.syncTasks.put(wrapper, new AtomicLong(wrapper.getRunnable().getInterval()));
                }
            }
        }
    }

    private void executeAsync(RunnableWrapper wrapper) {
        wrapper.getRunnable().prerunSync();
        final TridentRunnable runnable = wrapper.getRunnable();

        this.asyncReturns.put(this.getCachedAssignment(wrapper.getPlugin()).submit(new Callable() {
            @Override
            public Object call() throws Exception {
                runnable.run();
                runnable.runAfterAsync();

                return null;
            }
        }), wrapper);
    }

    private void executeSync(RunnableWrapper wrapper, boolean addToSync) {
        wrapper.getRunnable().prerunSync();
        wrapper.getRunnable().run();
        wrapper.getRunnable().runAfterSync();

        final TridentRunnable runnable = wrapper.getRunnable();

        Future<?> future = this.getCachedAssignment(wrapper.getPlugin()).submit(new Callable() {
            @Override
            public Object call() throws Exception {
                runnable.runAfterAsync();
                return null;
            }
        });

        if (addToSync) {
            this.syncReturns.add(new AbstractMap.SimpleEntry<Future<?>, String>(future,
                    wrapper.getPlugin().getDescription().name()));
        }
    }

    /**
     * Uses fast reflection to assign an ID to each runnable, that way there is no publicly exposed "setId()" that could
     * break things
     */
    private void assignId(TridentRunnable runnable) {
        FastClass.get(TridentRunnable.class).getField(runnable, "id").set(this.currentId);
        this.currentId++;
    }

    public void shutdown() {
        if (Trident.getServer().getConfig().getBoolean("in-a-hurry-mode")) {
            for (Map.Entry<RunnableWrapper, AtomicLong> entry : this.asyncTasks.entrySet()) {
                if (entry.getKey().getRunnable().usesInAHurry()) {
                    FastClass.get(TridentRunnable.class).getField(entry.getKey().getRunnable(), "inAHurry").set(true);
                    this.executeAsync(entry.getKey());
                }
            }

            for (Map.Entry<RunnableWrapper, AtomicLong> entry : this.syncTasks.entrySet()) {
                if (entry.getKey().getRunnable().usesInAHurry()) {
                    FastClass.get(TridentRunnable.class).getField(entry.getKey().getRunnable(), "inAHurry").set(true);
                    this.executeSync(entry.getKey(), true);
                }
            }

            for (Map.Entry<Future<?>, String> future : this.syncReturns) {
                try {
                    future.getKey().get(3L, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    // ignored exception
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    Trident.getLogger().warning("Runnable from " + future.getValue()
                            + " took more than 3 seconds in a hurry, going ahead with cancellation!");
                }
            }

            for (Map.Entry<Future<?>, RunnableWrapper> entry : this.asyncReturns.entrySet()) {
                try {
                    entry.getKey().get(3L, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    // ignored exception
                } catch (TimeoutException e) {
                    Trident.getLogger().warning("Runnable from " + entry.getValue().getPlugin().getDescription().name()
                            + " took more than 3 seconds in a hurry, going ahead with cancellation!");
                }
            }

            for (Map.Entry<Future<?>, RunnableWrapper> entry : this.asyncReturns.entrySet()) {
                if (entry.getKey().isDone()) {
                    entry.getValue().getRunnable().runAfterSync();
                    this.asyncReturns.remove(entry.getKey());
                }
            }
        }

        for (Map.Entry<ExecutorService, AtomicInteger> entry : this.threads.entrySet()) {
            entry.getKey().shutdownNow();
        }
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
            return this.repeating;
        }

        public TridentRunnable getRunnable() {
            return this.runnable;
        }

        public TridentPlugin getPlugin() {
            return this.plugin;
        }
    }
}
