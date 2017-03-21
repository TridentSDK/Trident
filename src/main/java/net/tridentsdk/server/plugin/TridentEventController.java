/*
 * Trident - A Multithreaded Server Alternative
 * Copyright 2017 The TridentSDK Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.tridentsdk.server.plugin;

import com.esotericsoftware.reflectasm.MethodAccess;
import lombok.Getter;
import net.tridentsdk.command.logger.Logger;
import net.tridentsdk.doc.Policy;
import net.tridentsdk.event.*;
import net.tridentsdk.plugin.SelfRegistered;
import net.tridentsdk.server.concurrent.PoolSpec;
import net.tridentsdk.server.concurrent.ServerThreadPool;

import javax.annotation.concurrent.ThreadSafe;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.function.Consumer;

/**
 * The implementation of the event instance
 */
@Policy("singleton")
@ThreadSafe
public final class TridentEventController implements EventController {
    /**
     * The thread to which the event execution is confined
     */
    private static final ServerThreadPool PLUGIN_EXECUTOR = ServerThreadPool.forSpec(PoolSpec.PLUGINS);
    /**
     * Singleton instance of the global event controller
     * instance.
     */
    @Getter
    private static final TridentEventController instance = new TridentEventController();

    /**
     * The mapping of all the event listeners to their
     * respective listener events.
     */
    private final ConcurrentMap<Class<? extends Event>, ConcurrentSkipListSet<EventDispatcher>> listeners = new ConcurrentHashMap<>();

    /**
     * Check if the class allows its members to be
     * registered automatically.
     *
     * @param cls the class to check
     * @return {@code true} if the class allows registration
     */
    private static boolean allow(Class<?> cls) {
        return cls.getAnnotation(SelfRegistered.class) == null;
    }

    @Override
    public void register(Object listener) {
        Class<?> cls = listener.getClass();
        if (!allow(cls)) {
            return;
        }

        MethodAccess access = MethodAccess.get(cls);
        Method[] methods = cls.getMethods();
        for (Method m : methods) {
            Parameter[] params = m.getParameters();
            if (params.length == 1) {
                Parameter p = params[0];

                // Check that it isn't listening to a
                // supertype
                Class<?> pType = p.getType();
                if (pType.isAnnotationPresent(Supertype.class)) {
                    new IllegalArgumentException("Attempted to register listener for supertype: " + pType.getSimpleName()).
                            printStackTrace();
                    continue;
                }

                // Check to make sure we are listenting to
                // an event
                if (Event.class.isAssignableFrom(pType)) {
                    Class<? extends Event> clazz = (Class<? extends Event>) pType;
                    ConcurrentSkipListSet<EventDispatcher> dispatchers = this.listeners.computeIfAbsent(
                            clazz, k -> new ConcurrentSkipListSet<>());
                    ListenerOpts opts = m.getAnnotation(ListenerOpts.class);
                    DispatchOrder order = DispatchOrder.MIDDLE;
                    if (opts != null) {
                        order = opts.order();
                    }

                    // Log to console if 2+ LAST listeners
                    // are registered
                    EventDispatcher last;
                    if (order == DispatchOrder.LAST &&
                            !dispatchers.isEmpty() &&
                            (last = dispatchers.last()).isLast()) {
                        Logger.get("Registrar").warn("Event listener \"" +
                                m.getName() + "\" will override the last event listener in " +
                                last.getContainer().getClass().getSimpleName() + ".java");
                    }

                    // Add to the dispatch queue
                    dispatchers.add(new EventDispatcher(access, listener, m, order));
                }
            }
        }
    }

    @Override
    public void unregister(Class<?> listener) {
        for (ConcurrentSkipListSet<EventDispatcher> queue : this.listeners.values()) {
            for (EventDispatcher dispatcher : queue) {
                if (dispatcher.isContainedBy(listener)) {
                    queue.remove(dispatcher);
                }
            }
        }
    }

    @Override
    public <T extends Event> void dispatch(T event) {
        ConcurrentSkipListSet<EventDispatcher> dispatchers = this.listeners.get(event.getClass());
        if (dispatchers != null) {
            CompletableFuture<T> future = CompletableFuture.completedFuture(event);
            for (EventDispatcher dispatcher : dispatchers) {
                future.thenApplyAsync(dispatcher::fire, PLUGIN_EXECUTOR).exceptionally(t -> {
                    t.printStackTrace();
                    return event;
                });
            }
        }
    }

    @Override
    public <T extends Event> void dispatch(T event, Consumer<T> callback) {
        ConcurrentSkipListSet<EventDispatcher> dispatchers = this.listeners.get(event.getClass());
        if (dispatchers != null) {
            CompletableFuture<T> future = CompletableFuture.completedFuture(event);
            for (EventDispatcher dispatcher : dispatchers) {
                future.thenApplyAsync(dispatcher::fire, PLUGIN_EXECUTOR).exceptionally(t -> {
                    t.printStackTrace();
                    return event;
                });
            }

            future.thenAcceptAsync(callback, PLUGIN_EXECUTOR);
        }
    }
}