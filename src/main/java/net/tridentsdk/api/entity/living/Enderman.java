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
package net.tridentsdk.api.entity.living;

import net.tridentsdk.api.entity.Neutral;

/**
 * Represents an Enderman
 *
 * @author TridentSDK Team
 */
public interface Enderman extends Neutral {
    /**
     * Get the block that this enderman is currently carrying
     *
     * @return the block that this entity is carrying
     */
    Object getBlockCarried();   /* TODO: Replace Object with valid implementation of BlockState */

    /**
     * Gets the number of endermites spawned by this enderman. Affects spawn chance of other endermites
     *
     * @return the number of endermites spawned by this enderman
     */
    int getEndermiteCount();
}
