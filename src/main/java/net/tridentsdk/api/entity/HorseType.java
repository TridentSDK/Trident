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

public enum HorseType {
    /**
     * Generic Horse
     */
    HORSE(0),

    /**
     * Donkey
     */
    DONKEY(1),

    /**
     * Mule
     */
    MULE(2),

    /**
     * Zombie horse
     */
    ZOMBIE(3),

    /**
     * Skeleton horse
     */
    SKELETON(4);
    private static final HorseType[] byId = new HorseType[5];

    static {
        for (HorseType type : HorseType.values()) {
            byId[type.id] = type;
        }
    }

    private final int id;

    HorseType(int id) {
        this.id = id;
    }

}
