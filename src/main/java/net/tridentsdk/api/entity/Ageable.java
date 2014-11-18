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
 * Represents a LivingEntity that has an age and has the ability to bread
 *
 * @author TridentSDK Team
 */
public interface Ageable extends LivingEntity {
    /**
     * The current age of this entity, in ticks
     *
     * @return the age of this entity
     */
    int getAge();

    /**
     * Set the current age of this entity, in ticks
     *
     * @param ticks the age to set
     */
    void setAge(int ticks);

    /**
     * Whether or not this entity can breed or not, where the ability to breed represents whether or not this entity can
     * become "in love"
     *
     * @return whether or not this entity can be bred
     */
    boolean canBreed();

    /**
     * Whether or not this entity is "in love", such that it will actively display the particle effect for breeding
     * hearts and search for a mate
     *
     * @return whether or not this entity is in love
     */
    boolean isInLove();
}
