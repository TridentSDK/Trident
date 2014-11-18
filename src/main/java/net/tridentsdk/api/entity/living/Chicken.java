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

import net.tridentsdk.api.entity.Ageable;
import net.tridentsdk.api.entity.Peaceful;

/**
 * Represents a Chicken
 *
 * @author TridentSDK Team
 */
public interface Chicken extends Ageable, Peaceful {
    /**
     * Whether or not this Chicken is a 'Chicken Jockey', defined by whether or not this Chicken will naturally despawn
     *
     * @return whether or not this Chicken is a 'Chicken Jockey'
     */
    boolean isChickenJockey();

    /**
     * Ticks until this Chicken will lay its egg
     *
     * @return the number of ticks until this Chicken will lay an egg
     */
    int getEggTicks();
}
