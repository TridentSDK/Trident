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

package net.tridentsdk.server.window;

import com.google.common.collect.Collections2;
import net.tridentsdk.window.Window;
import net.tridentsdk.window.WindowHandler;

import javax.annotation.concurrent.ThreadSafe;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages the inventory windows on the server, whether being viewed or not
 *
 * @author The TridentSDK Team
 */
@ThreadSafe
public class TridentWindowHandler implements WindowHandler {
    private static final Map<Integer, TridentWindow> windows = new ConcurrentHashMap<>();

    @Override
    public Window windowBy(int id) {
        return windows.get(id);
    }

    @Override
    public void registerWindow(Window window) {
        windows.put(window.windowId(), (TridentWindow) window);
    }

    @Override
    public Collection<Window> windows() {
        return Collections2.transform(windows.values(), (w) -> w);
    }
}
