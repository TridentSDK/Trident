package net.tridentsdk.server.threads;

import net.tridentsdk.api.threads.TaskExecutor;
import net.tridentsdk.plugin.TridentPlugin;

import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class PluginThreads {
    static final ConcurrentTaskExecutor<TridentPlugin> THREAD_MAP = new ConcurrentTaskExecutor<>(2);
    static final ConcurrentCache<TridentPlugin, TaskExecutor> CACHE_MAP = new ConcurrentCache<>();

    static final ExecutorService SERVICE = Executors.newSingleThreadExecutor();

    private PluginThreads() {
    }

    /**
     * Gets the management tool for the plugin
     *
     * <p>May block the first call</p>
     *
     * @param plugin the plugin to find the wrapper for
     * @return the plugin thread handler
     */
    public static TaskExecutor pluginThreadHandle(final TridentPlugin plugin) {
        return PluginThreads.CACHE_MAP.retrieve(plugin, new Callable<TaskExecutor>() {
            @Override
            public TaskExecutor call() throws Exception {
                TaskExecutor executor = PluginThreads.THREAD_MAP.getScaledThread();
                PluginThreads.THREAD_MAP.assign(executor, plugin);

                return executor;
            }
        }, EntityThreads.SERVICE);
    }

    /**
     * Decaches the plugin handler from the mappings
     *
     * @param plugin the plugin to remove from the cache
     */
    public static void remove(TridentPlugin plugin) {
        PluginThreads.THREAD_MAP.removeAssignment(plugin);
        PluginThreads.CACHE_MAP.remove(plugin);
    }

    /**
     * Gets all of the thread plugin wrappers
     *
     * @return the values of the concurrent cache
     */
    public static Collection<TridentPlugin> wrappedPlayers() {
        return PluginThreads.THREAD_MAP.values();
    }
}
