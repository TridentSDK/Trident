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
package net.tridentsdk.entity.block;

import net.tridentsdk.api.entity.Hanging;
import net.tridentsdk.api.inventory.ItemStack;

/**
 * Represents an ItemFrame
 *
 * @author TridentSDK Team
 */
public interface ItemFrame extends Hanging {
    /**
     * Get the current ItemStack this ItemFrame has
     *
     * @return the current ItemStack this ItemFrame has
     */
    ItemStack getCurrentItem();

    /**
     * Get the rotation of this ItemFrame's ItemStack This is the number of times this has been rotated 45 degrees
     *
     * @return the rotation of this ItemFrame's ItemStack
     */
    byte getItemStackRotation();
}
