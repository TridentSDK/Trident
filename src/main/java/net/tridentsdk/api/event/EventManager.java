/*
 * Trident - A Multithreaded Server Alternative
 * Copyright 2014 The TridentSDK Team
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
package net.tridentsdk.api.event;

import com.google.common.collect.Maps;
import net.tridentsdk.api.Trident;
import net.tridentsdk.api.reflect.FastClass;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.EventListener;
import java.util.List;
import java.util.Map;

public class EventManager {
    private final Map<Class<? extends Event>, List<RegisteredListener>> callers = Maps.newHashMap();

    public EventManager() {
        if (!Trident.isTrident()) {
            throw new UnsupportedOperationException("EventManager must be initiated by TridentSDK!");
        }
    }

    /**
     * Normally not needed to be used. Plugin listeners are automatically registered when they are loaded.
     *
     * @param listener the listener instance to use to register
     */
    public void registerListener(Listener listener) {
        FastClass fastClass = FastClass.get(listener.getClass());

        for (Method method : listener.getClass().getDeclaredMethods()) {
            Class<?>[] parameterTypes = method.getParameterTypes();

            if (parameterTypes.length != 1 || !EventListener.class.isAssignableFrom(parameterTypes[0])) {
                continue;
            }

            Class<? extends Event> eventClass = parameterTypes[0].asSubclass(Event.class);
            EventHandler handler = method.getAnnotation(EventHandler.class);
            Importance importance = handler == null ? Importance.MEDIUM : handler.importance();

            List<RegisteredListener> eventCallers = this.callers.get(eventClass);
            eventCallers.add(new RegisteredListener(fastClass.getMethod(listener, method.getName()), eventClass, importance));
            Collections.sort(eventCallers);
            callers.put(eventClass, eventCallers);
        }
    }

    /**
     * Calls an event
     *
     * @param event the event to call
     */
    public void call(Event event) {
        for (RegisteredListener listener : this.callers.get(event.getClass()))
            listener.execute(event);
    }
}
