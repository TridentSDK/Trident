/*
 * Trident - A Multithreaded Server Alternative
 * Copyright 2016 The TridentSDK Team
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

import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import lombok.Getter;
import net.tridentsdk.doc.Policy;
import net.tridentsdk.event.Event;
import net.tridentsdk.event.EventController;
import net.tridentsdk.event.Supertype;
import net.tridentsdk.plugin.SelfRegistered;

import javax.annotation.concurrent.ThreadSafe;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Queue;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

/**
 * The implementation of the event instance
 */
// TODO
@Policy("singleton")
@ThreadSafe
public final class TridentEventController implements EventController {
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
    private static final ConcurrentMap<Class<? extends Event>, Queue<EventDispatcher>> listeners =
            Maps.newConcurrentMap();

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

    /**
     * Check if the method allows itself to be registered
     * automatically.
     *
     * @param method the method to check
     * @return {@code true} if the method allows
     * registration
     */
    private static boolean allow(Method method) {
        return method.getAnnotation(SelfRegistered.class) == null;
    }

    @Override
    public void register(Object listener) {
        Class<?> cls = listener.getClass();
        if (!allow(cls)) {
            return;
        }

        Method[] methods = cls.getDeclaredMethods();
        for (Method m : methods) {
            if (!allow(m)) {
                continue;
            }

            Parameter[] params = m.getParameters();
            if (params.length == 1) {
                Parameter p = params[0];
                Class<?> pType = p.getType();
                if (Event.class.isAssignableFrom(pType)
                        && pType.getAnnotation(Supertype.class) == null) {
                    Queue<EventDispatcher> dispatchers = listeners.computeIfAbsent(
                            (Class<? extends Event>) pType, k -> Queues.newConcurrentLinkedQueue());
                }
            }
        }
    }

    @Override
    public void unregister(Object listener) {

    }

    @Override
    public <T extends Event> void dispatch(T event, Consumer<T> callback) {
    }
}