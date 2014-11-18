/*
 *     Trident - A Multithreaded Server Alternative
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
package net.tridentsdk.api.entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.Collection;
import java.util.Map;

public enum VillagerCareer {
    /**
     * Fletcher
     */
    FLETCHER(VillagerProfession.FARMER, 0),

    /**
     * Farmer
     */
    FARMER(VillagerProfession.FARMER, 1),

    /**
     * Fisherman
     */
    FISHERMAN(VillagerProfession.FARMER, 2),

    /**
     * Shepherd
     */
    SHEPHERD(VillagerProfession.FARMER, 3),

    /**
     * Librarian
     */
    LIBRARIAN(VillagerProfession.LIBRARAIAN, 0),

    /**
     * Cleric
     */
    CLERIC(VillagerProfession.PRIEST, 0),

    /**
     * Tool smith
     */
    TOOL_SMITH(VillagerProfession.BLACKSMITH, 0),

    /**
     * Armorer
     */
    ARMORER(VillagerProfession.BLACKSMITH, 1),

    /**
     * Weapon smith
     */
    WEAPON_SMITH(VillagerProfession.BLACKSMITH, 2),

    /**
     * Butcher
     */
    BUTCHER(VillagerProfession.BUTCHER, 0),

    /**
     * Leatherworker
     */
    LEATHERWORKER(VillagerProfession.BUTCHER, 1);
    private static final Map<VillagerProfession, Collection<VillagerCareer>> byProfession = Maps.newHashMap();

    static {
        Collection<VillagerCareer> parentColl = Lists.newArrayList();
        VillagerProfession current = null;
        for (VillagerCareer carrer : VillagerCareer.values()) {
            if (current == null) {
                current = carrer.parent;
                parentColl.add(carrer);
                continue;
            }
            if (current == carrer.parent) {
                parentColl.add(carrer);
            } else {
                byProfession.put(current, parentColl);
                parentColl = Lists.newArrayList();
                current = carrer.parent;
            }
        }
    }

    private final VillagerProfession parent;
    private final int id;

    VillagerCareer(VillagerProfession parent, int id) {
        this.parent = parent;
        this.id = id;
    }

}
