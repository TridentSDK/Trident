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
package net.tridentsdk.entity.projectile;

import net.tridentsdk.api.entity.Projectile;

/**
 * A hook at the end of the fishing rod that can catch fish or damage entities
 *
 * @author The TridentSDK Team
 */
public interface FishHook extends Projectile {
    /**
     * The chance that a fish will be caught on the hook
     * <p/>
     * <p>Works only in water</p>
     *
     * @return the chance a fish will be caught on the hook
     */
    float getBiteChance();

    /**
     * Sets the chance the fish will bite the hook
     *
     * @param chance the chance the fish will bite the hook
     * @return unspecified TODO
     */
    float setBiteChance(float chance);
}
