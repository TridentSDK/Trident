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

import net.tridentsdk.api.event.*;
import net.tridentsdk.api.event.Event;
import net.tridentsdk.api.reflect.FastMethod;

public class RegisteredListener implements Comparable<RegisteredListener> {
    private final FastMethod method;
    private final Class<? extends net.tridentsdk.api.event.Event> eventClass;
    private final net.tridentsdk.api.event.Importance importance;

    RegisteredListener(FastMethod method, Class<? extends net.tridentsdk.api.event.Event> eventClass, net.tridentsdk.api.event.Importance importance) {
        this.method = method;
        this.eventClass = eventClass;
        this.importance = importance;
    }

    public FastMethod getMethod() {
        return this.method;
    }

    public Class<? extends net.tridentsdk.api.event.Event> getEventClass() {
        return this.eventClass;
    }

    public net.tridentsdk.api.event.Importance getImportance() {
        return this.importance;
    }

    public void execute(Event event) {
        this.method.invoke(event);
    }

    @Override public int compareTo(RegisteredListener registeredListener) {
        return importance.compareTo(registeredListener.getImportance());
    }
}
