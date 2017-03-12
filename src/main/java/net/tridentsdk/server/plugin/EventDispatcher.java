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
import net.tridentsdk.event.DispatchOrder;
import net.tridentsdk.event.Event;

import javax.annotation.concurrent.Immutable;
import java.lang.reflect.Method;

/**
 * A container class that links the controller to the event
 * listener in order to dispatch events to it.
 */
@Immutable
public final class EventDispatcher implements Comparable<EventDispatcher> {
    private final MethodAccess access;
    @Getter
    private final Object container;
    private final int idx;
    private final DispatchOrder order;

    /**
     * Creates a new event dispatcher.
     *  @param access the accessor to the class methods
     * @param inst the instance of the listener
     * @param method the method to invoke
     * @param order the order which to invoke the listener
     */
    public EventDispatcher(MethodAccess access, Object inst, Method method, DispatchOrder order) {
        this.access = access;
        this.container = inst;
        this.order = order;

        System.out.println("REG M " + method.getName());
        this.idx = access.getIndex(method.getName(), method.getParameterTypes());
    }

    /**
     * Fires the event handler, causing it to invoke the
     * method which processes the given event object.
     *
     * <p>It is up to the responsibility of the bookkeeping
     * class to ensure type safety.</p>
     *
     * @param event the event to pass to the handler
     * @return the event that was passed to this handler
     */
    public Event fire(Event event) {
        this.access.invoke(this.container, this.idx, event);
        return event;
    }

    /**
     * Checks to determine whether or not the event handler
     * that is represented by this invoker is contained by
     * the given class.
     *
     * @param cls the class to check
     * @return {@code true} if the given class contains this
     * event handler
     */
    public boolean isContainedBy(Class<?> cls) {
        return this.container.getClass().equals(cls);
    }

    /**
     * Obtains whether this listener is the last to be
     * invoked on dispatch of an event.
     *
     * @return {@code true} if this is the last listener
     */
    public boolean isLast() {
        return this.order == DispatchOrder.LAST;
    }

    @Override
    public int compareTo(EventDispatcher o) {
        // Since we're dealing with an ordered set, we need
        // to return 1 if equal to retain the last listener
        if (this.order == o.order) {
            return 1;
        }

        return this.order.compareTo(o.order);
    }
}