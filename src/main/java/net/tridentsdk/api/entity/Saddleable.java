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
 * Represents a living entity that can wear a saddle
 *
 * @author TridentSDK Team
 */
public interface Saddleable extends LivingEntity {

    /**
     * Whether this entity is saddled or not
     *
     * @return whether or not this entity has a saddle
     */
    boolean isSaddled();

    /**
     * Set whether or not this entity is saddled
     *
     * @param saddled whether this entity should be saddled or not
     */
    void setSaddled(boolean saddled);
}
