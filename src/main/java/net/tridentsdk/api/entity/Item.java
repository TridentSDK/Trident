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
