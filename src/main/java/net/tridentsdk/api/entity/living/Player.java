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

import net.tridentsdk.api.CommandIssuer;
import net.tridentsdk.api.GameMode;
import net.tridentsdk.api.Messagable;
import net.tridentsdk.api.entity.LivingEntity;
import net.tridentsdk.api.inventory.ItemStack;

import java.util.Locale;

/**
 * Represents a player entity after joining the impl
 *
 * @author The TridentSDK Team
 */
public interface Player extends LivingEntity, Messagable, CommandIssuer {

    /**
     * Returns the flying speed of the Player
     *
     * @return float flying speed of the Player
     */
    float getFlyingSpeed();

    /**
     * Set the flying speed of the Player
     *
     * @param flyingSpeed float flying speed of the Player
     */
    void setFlyingSpeed(float flyingSpeed);

    // TODO: Use word settings?

    /**
     * Returns the Player's {@link java.util.Locale} settings
     *
     * @return Locale the Player's Locale settings
     */
    Locale getLocale();

    /**
     * Returns the ItemStack in the Player's hand
     *
     * @return ItemStack current ItemStack in the Player's hand
     */
    ItemStack getItemInHand();

    /**
     * Returns the GameMode the Player is in
     *
     * @return GameMode current GameMode of the Player
     */
    GameMode getGameMode();

    /**
     * Returns the move speed of the player
     *
     * @return float the Player's current move speed
     */
    float getMoveSpeed();

    /**
     * Sets the Player's move speed
     *
     * @param speed float Player's move speed
     */
    void setMoveSpeed(float speed);

    /**
     * Returns the sneak speed of the player
     *
     * @return float the Player's current sneak speed
     */
    float getSneakSpeed();

    /**
     * Sets the Player's sneak speed
     *
     * @param speed float Player's sneak speed
     */
    void setSneakSpeed(float speed);

    /**
     * Returns the walk speed of the player
     *
     * @return float the Player's current walk speed
     */
    float getWalkSpeed();

    /**
     * Sets the Player's walk speed
     *
     * @param speed float Player's walk speed
     */
    void setWalkSpeed(float speed);
}
