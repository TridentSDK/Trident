/*
 *     TridentSDK - A Minecraft Server API
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


package net.tridentsdk.event;

import com.google.common.collect.Maps;
import net.tridentsdk.api.Trident;
import net.tridentsdk.api.event.*;
import net.tridentsdk.api.event.Event;
import net.tridentsdk.api.event.EventHandler;
import net.tridentsdk.api.event.Importance;
import net.tridentsdk.api.reflect.FastClass;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EventListener;
import java.util.List;
import java.util.Map;

public class EventManager {
    private final Map<Class<? extends net.tridentsdk.api.event.Event>, List<RegisteredListener>> callers = Maps.newHashMap();

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
    public void registerListener(net.tridentsdk.api.event.Listener listener) {
        FastClass fastClass = FastClass.get(listener.getClass());

        for (Method method : listener.getClass().getDeclaredMethods()) {
            Class<?>[] parameterTypes = method.getParameterTypes();

            if (parameterTypes.length != 1 || !EventListener.class.isAssignableFrom(parameterTypes[0])) {
                continue;
            }

            Class<? extends net.tridentsdk.api.event.Event> eventClass = parameterTypes[0].asSubclass(net.tridentsdk.api.event.Event.class);
            net.tridentsdk.api.event.EventHandler handler = method.getAnnotation(EventHandler.class);
            net.tridentsdk.api.event.Importance importance = handler == null ? Importance.MEDIUM : handler.importance();

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
