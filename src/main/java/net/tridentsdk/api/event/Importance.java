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
