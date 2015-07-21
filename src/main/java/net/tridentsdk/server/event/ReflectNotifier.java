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

package net.tridentsdk.server.event;

import com.esotericsoftware.reflectasm.MethodAccess;
import net.tridentsdk.event.Event;
import net.tridentsdk.event.EventNotifier;
import net.tridentsdk.event.Importance;
import net.tridentsdk.event.Listener;
import net.tridentsdk.plugin.Plugin;

import java.util.Comparator;

/**
 * A fast-reflection based event invoker which notifies event listeners
 *
 * @author The TridentSDK Team
 * @since 0.4-alpha
 */
public class ReflectNotifier implements EventNotifier, Comparator<ReflectNotifier> {
    private final MethodAccess handle;
    private final Plugin plugin;
    private final int index;
    private final Listener instance;
    private final Class<? extends Event> eventClass;
    private final Importance importance;

    ReflectNotifier(MethodAccess handle, Plugin plugin, int index, Listener instance,
                    Class<? extends Event> eventClass, Importance importance) {
        this.handle = handle;
        this.plugin = plugin;
        this.index = index;
        this.instance = instance;
        this.eventClass = eventClass;
        this.importance = importance;
    }

    public MethodAccess method() {
        return this.handle;
    }

    public int index() {
        return this.index;
    }

    @Override
    public Plugin plugin() {
        return plugin;
    }

    @Override
    public Class<? extends Event> eventType() {
        return this.eventClass;
    }

    @Override

    public Importance importance() {
        return this.importance;
    }

    @Override
    public void handle(Event event) {
        this.handle.invoke(this.instance, this.index, event);
    }

    @Override
    public Listener listener() {
        return this.instance;
    }

    @Override
    public int compare(ReflectNotifier registeredListener, ReflectNotifier t1) {
        return registeredListener.importance().compareTo(t1.importance());
    }
}
