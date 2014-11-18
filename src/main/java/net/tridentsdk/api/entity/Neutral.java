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
 * Represents a neutral entity Purpose of interface is to provide ease-of-access to large groups of a single type (i.e.
 * 'Hostiles', 'Friendlies')
 *
 * @author TridentSDK Team
 */
public interface Neutral extends LivingEntity {
    /**
     * Whether or not this entity has been angered. Note, not all neutral entities can be angered. When an entity is
     * angered, it is considered hostile
     *
     * @return Whether this entity is angered or not
     */
    boolean isHostile();
}
