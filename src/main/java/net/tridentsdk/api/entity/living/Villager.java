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
 * Represents a Villager
 *
 * @author TridentSDK Team
 */
public interface Villager extends Ageable, Tradeable, Peaceful {
    /**
     * The profession of this villager
     *
     * @return the profession of this villager
     */
    VillagerProfession getProfession();

    /**
     * Sets the profession of this villager. If the current career does not have the profession as its parent, the
     * current career will be to the first available career
     */
    void setProfession(VillagerProfession profession);

    /**
     * The career of this villager
     *
     * @return the career of this villager
     */
    VillagerCareer getCareer();

    /**
     * Sets the career of this villager. If the profession does not match the specified career's parent profession, the
     * profession will be set the career's parent profession
     *
     * @param career the career you want to set for this villager
     */
    void setCareer(VillagerCareer career);

    /**
     * The current level of this villager's career. Affects trades offered by this villager
     *
     * @return the current level of this villager's career
     */
    int getCareerLevel();
}
