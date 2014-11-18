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
package net.tridentsdk.api.entity;

/**
 * Represents the generic Minecart
 *
 * @author TridentSDK Team
 */
public interface MinecartBase extends Entity {
    /**
     * Represents this Minecart's display tile, in the form of a BlockState
     *
     * @return the display tile of this Minecart
     */
    Object getDisplayTile();    /* TODO: Change return type to valid implementation of BlockState */

    /**
     * Set this Minecart's display tile to the specified block state
     *
     * @param blockState the state to set this to
     */
    void setDisplayTile(Object blockState);    /* TODO: Change param type to valid implementation of BlockState */

    /**
     * Get the offset for this Minecart's display tile
     *
     * @return the offset for this Minecart's display tile
     */
    int getDisplayTileOffset();

    /**
     * Set the offset for this Minecart's display tile
     *
     * @param offset the offset to set
     */
    void setDisplayTileOffset(int offset);

    /**
     * Gets the custom name of this Minecart
     *
     * @return the custom name of this Minecart
     */
    String getName();

    /**
     * Sets the custom name of this Minecart
     *
     * @param name the new value of the custom name
     */
    void setName(String name);
}
