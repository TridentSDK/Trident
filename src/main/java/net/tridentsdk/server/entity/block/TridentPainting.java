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
package net.tridentsdk.server.entity.block;

import net.tridentsdk.Block;
import net.tridentsdk.Location;
import net.tridentsdk.entity.EntityProperties;
import net.tridentsdk.entity.block.Painting;
import net.tridentsdk.server.entity.TridentEntity;

import java.util.UUID;

public class TridentPainting extends TridentEntity implements Painting {

    public TridentPainting(UUID uniqueId, Location spawnLocation) {
        super(uniqueId, spawnLocation);
    }

    @Override
    public String getMotive() {
        return null;
    }

    @Override
    public Block getBlockPlacedOn() {
        return null;
    }

    @Override
    public boolean isNameVisible() {
        return false;
    }

    @Override
    public void applyProperties(EntityProperties properties) {

    }
}
