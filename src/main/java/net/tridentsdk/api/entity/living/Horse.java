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

import net.tridentsdk.api.entity.*;

/**
 * Represents a Horse
 *
 * @author TridentSDK Team
 */
public interface Horse extends Tameable, Saddleable, InventoryHolder, Peaceful {
    /**
     * What breed of horse this is
     *
     * @return the HorseType that represents this breed
     */
    HorseType getBreed();

    /**
     * Whether or not this horse is grazing
     *
     * @return if this horse is grazing or not
     */
    boolean isGrazing();

    /**
     * The temper of this horse, higher temper dictates that the horse is easier to tame
     *
     * @return the temper of this horse. Range of 0-100
     */
    int getTemper();

    /**
     * Whether or not this horse has a chest
     *
     * @return false if this horse's breed is not a donkey or mule, or this horse has no chest
     */
    boolean hasChest();

    /**
     * The variant of this horse, will return an invalid variant if this horse is not of a HORSE breed
     *
     * @return the variant of this horse
     */
    HorseVariant getVariant();
}
