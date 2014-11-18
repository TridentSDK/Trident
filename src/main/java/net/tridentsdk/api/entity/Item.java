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
 * Represents a dropped ItemStack
 *
 * @author TridentSDK Team
 */
public interface Item extends Entity {
    /**
     * Represents the age of this Item entity
     *
     * @return the age of this Item entity
     */
    int getAge();

    /**
     * Sets the age of this Item entity
     *
     * @param age the age to set
     */
    void setAge(int age);

    /**
     * Represents the health of this Item entity.
     *
     * @return the health of this Item entity
     */
    short getHealth();

    /**
     * Sets the health for this Item entity
     *
     * @param health the value to set the health to
     */
    void setHealth(short health);

    /**
     * Represents the owner of this Item entity
     */
    String getOwner();

    /**
     * Sets the owner of this Item entity. Nobody else can pickup this Item until 10 seconds are left in its life if
     * this value is set
     *
     * @param owner The name of the Player to set this too
     */
    void setOwner(String owner);

    /**
     * Gets the name of the Player that dropped this Will return {@code null} if this was spawned unnaturally
     *
     * @return the name of the Player that dropped this
     */
    String getDropper();

    /**
     * Sets the Player who dropped this Item
     *
     * @param dropper the name of the Player who will become the dropper
     */
    void setDropper(String dropper);
}
