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

import net.tridentsdk.api.reflect.FastMethod;

public class RegisteredListener implements Comparable<RegisteredListener> {
    private final FastMethod method;
    private final Class<? extends Event> eventClass;
    private final Importance importance;

    RegisteredListener(FastMethod method, Class<? extends Event> eventClass, Importance importance) {
        this.method = method;
        this.eventClass = eventClass;
        this.importance = importance;
    }

    public FastMethod getMethod() {
        return this.method;
    }

    public Class<? extends Event> getEventClass() {
        return this.eventClass;
    }

    public Importance getImportance() {
        return this.importance;
    }

    public void execute(Event event) {
        this.method.invoke(event);
    }

    @Override public int compareTo(RegisteredListener registeredListener) {
        return importance.compareTo(registeredListener.getImportance());
    }
}
