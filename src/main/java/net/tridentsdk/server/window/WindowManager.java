/*
 *     Trident - A Multithreaded Server Alternative
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
package net.tridentsdk.server.window;

import net.tridentsdk.window.Window;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages the inventory windows on the server, whether being viewed or not
 *
 * @author The TridentSDK Team
 */
public class WindowManager {
    private static final Map<Integer, TridentWindow> windows = new ConcurrentHashMap<>();

    /**
     * Gets a window by its ID
     *
     * @param id the ID of a window
     * @return the window with the ID, or {@code null} if it doesn't exist
     */
    public Window getWindow(int id) {
        return windows.get(id);
    }

    /**
     * Registers the window with the manager
     *
     * @param window the window to be registered
     */
    public void registerWindow(TridentWindow window) {
        windows.put(window.getId(), window);
    }

    /**
     * Gets all registered windows with the manager
     *
     * @return the windows registered
     */
    public Collection<TridentWindow> getWindows() {
        return windows.values();
    }
}
