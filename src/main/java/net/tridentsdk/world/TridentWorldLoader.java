/*
 * Copyright (C) 2014 The TridentSDK Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.tridentsdk.world;

import net.tridentsdk.api.world.*;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

public abstract class TridentWorldLoader implements WorldLoader {

    private final Iterable<AtomicReference<World>> worldReferences = new ArrayList<>();

    @Override
    public World load(String world) {
        return new TridentWorld(world, this);
    }

    @Override
    public void save(World world) {
        // TODO
    }

    @Override
    public boolean worldExists(String world) {
        for (AtomicReference<World> reference : this.worldReferences) {
            if (reference.get().getName().equalsIgnoreCase(world)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean chunkExists(World world, int x, int z) {
        for (Chunk chunk : ((TridentWorld) world).chunks) {
            if ((chunk.getX() == x) && (chunk.getZ() == z)) {
                return true;
            }
        }

        return false;
    }
}
