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

import net.tridentsdk.api.entity.Equippable;
import net.tridentsdk.api.entity.Hostile;

/**
 * Represents a Zombie
 *
 * @author TridentSDK Team
 */
public interface Zombie extends Hostile, Equippable {
    /**
     * Represents if this zombie is a Zombie Villager or not
     *
     * @return whether or not this is a zombie villager
     */
    boolean isVillager();

    /**
     * Represents if this zombie is a baby zombie or not
     *
     * @return whether or not this zombie is a baby zombie
     */
    boolean isBaby();
}
