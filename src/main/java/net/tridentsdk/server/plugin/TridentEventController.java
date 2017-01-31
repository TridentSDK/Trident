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

import lombok.Getter;
import net.tridentsdk.doc.Policy;
import net.tridentsdk.event.Event;
import net.tridentsdk.event.EventController;

import javax.annotation.concurrent.ThreadSafe;

/**
 * The implementation of the event instance
 */
@Policy("singleton")
@ThreadSafe
public final class TridentEventController implements EventController {
    // TODO
    /**
     * Singleton instance of the global event controller
     * instance.
     */
    @Getter
    private static final TridentEventController instance = new TridentEventController();

    @Override
    public void register(Object listener) {

    }

    @Override
    public void unregister(Object listener) {

    }

    @Override
    public void dispatch(Event event) {

    }
}