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

public enum VillagerProfession {
    /**
     * Farmer
     */
    FARMER(0),

    /**
     * Librarian
     */
    LIBRARAIAN(1),

    /**
     * Priest
     */
    PRIEST(2),

    /**
     * Blacksmith
     */
    BLACKSMITH(3),

    /**
     * Butcher
     */
    BUTCHER(4);
    private static final VillagerProfession[] byId = new VillagerProfession[5];

    static {
        for (VillagerProfession profession : VillagerProfession.values()) {
            byId[profession.id] = profession;
        }
    }

    private final int id;

    VillagerProfession(int id) {
        this.id = id;
    }

    /**
     * Returns the {@code int} value of the VillagerProfession
     *
     * @return {@code int} value of the VillagerProfession
     */
    public int toInt() {
        return this.id;
    }

    /**
     * Returns the {@code int} value of the VillagerProfession
     *
     * @param villagerProfession VillagerProfession
     * @return {@code int} value of the VillagerProfession
     */
    public static int toInt(VillagerProfession villagerProfession) {
        return villagerProfession.toInt();
    }
}
