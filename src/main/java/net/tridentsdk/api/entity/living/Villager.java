/*
 *     TridentSDK - A Minecraft Server API
 *     Copyright (C) 2014, The TridentSDK Team
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
