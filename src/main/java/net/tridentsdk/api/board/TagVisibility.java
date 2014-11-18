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
package net.tridentsdk.api.board;

/**
 * Visibility flag of the player tag
 *
 * @author The TridentSDK Team
 */
public enum TagVisibility {
    /**
     * Always visible
     */
    ALWAYS("always"),
    /**
     * Hidden from the other team
     */
    HIDE_OTHER_TEAMS("hideFromOtherTeams"),
    /**
     * Hidden from own team
     */
    HIDE_OWN_TEAM("hideFromOwnTeam"),
    /**
     * Never shown
     */
    NEVER("never");

    private final String s;

    TagVisibility(String s) {
        this.s = s;
    }

    @Override
    public String toString() {
        return this.s;
    }
}
