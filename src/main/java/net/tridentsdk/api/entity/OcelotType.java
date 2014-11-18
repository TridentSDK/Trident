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
 * Represents the type of an Ocelot
 *
 * @author TridentSDK Team
 */
public enum OcelotType {
    /**
     * Wild
     */
    WILD(0),

    /**
     * Tuxedo
     */
    TUXEDO(1),

    /**
     * Tabby
     */
    TABBY(2),

    /**
     * Siamese
     */
    SIAMESE(3);

    private static final OcelotType[] byId = new OcelotType[4];

    static {
        for (OcelotType type : OcelotType.values()) {
            byId[type.id] = type;
            // TODO by ordinal?
        }
    }

    private final int id;

    OcelotType(int id) {
        this.id = id;
    }

}
