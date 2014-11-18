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
package net.tridentsdk.api.event;

public enum Importance {
    /*
     * Levels of priority an event is given, where the events fire from LOWEST to HIGHEST
     */

    LOWEST(0), LOW(1), MEDIUM(2), HIGH(3), HIGHEST(4);

    private final int importance;

    Importance(int level) {
        this.importance = level;
    }

    /**
     * @return return the importance level
     */

    public int getImportance() {
        return this.importance;
    }
}
