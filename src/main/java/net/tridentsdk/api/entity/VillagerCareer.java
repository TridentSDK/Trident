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
