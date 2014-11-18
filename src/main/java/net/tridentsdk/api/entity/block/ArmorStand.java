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
package net.tridentsdk.api.entity.block;

import net.tridentsdk.api.entity.Equippable;
import net.tridentsdk.api.entity.SlotProperties;
import net.tridentsdk.api.util.PartRotation;

/**
 * Represents an Armor Stand
 *
 * @author TridentSDK Team
 */
public interface ArmorStand extends Equippable {
    /**
     * Gets the slot properties of this Armor Stand
     *
     * @return this armor stand's slot properties
     */
    SlotProperties getSlotProperties();

    /**
     * Whether or not this Armor Stand is invisible
     *
     * @return whether or not this Armor Stand is invisible
     */
    boolean isInvisible();

    /**
     * Whether or not this Armor Stand should display its baseplate
     *
     * @return whether or not this Armor Stand should display its baseplate
     */
    boolean displayBaseplate();

    /**
     * Whether or not this Armor Stand should display its arms
     *
     * @return whether or not this Armor Stand should display its arms
     */
    boolean displayArms();

    /**
     * Whether or not this Armor Stand will fall or not
     *
     * @return whether or not this Armor Stand will fall or not
     */
    boolean useGravity();

    /**
     * Returns the pose for this Armor Stand
     *
     * @return the post of this Armor Stand
     * @deprecated Uses magic numbers for indexing, exists until another way is pushed
     */
    @Deprecated
    PartRotation[] getPose();

    /**
     * Whether or not this Armor Stand is small
     *
     * @return whether or not this Armor Stand is small
     */
    boolean isTiny();
}
