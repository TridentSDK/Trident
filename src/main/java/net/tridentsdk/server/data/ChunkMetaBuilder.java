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

package net.tridentsdk.server.data;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.world.ChunkLocation;

/**
 * Builds chunk metadata required for chunk packet data sent to the player
 *
 * @author The TridentSDK Team
 */
public class ChunkMetaBuilder implements Writable {
    private ChunkLocation location;
    private short bitmap;

    /**
     * Gets the chunk location
     *
     * @return the location of the chunk built into the metadata
     */
    public ChunkLocation getLocation() {
        return this.location;
    }

    /**
     * Sets the chunk meta location
     *
     * @param location the location to set the chunk to
     * @return the current instance
     */
    public ChunkMetaBuilder location(ChunkLocation location) {
        this.location = location;

        return this;
    }

    /**
     * Reads the {@code short} value that maps the bits in the chunk
     *
     * @return the chunk bitmap
     */
    public short getBitmap() {
        return this.bitmap;
    }

    /**
     * Sets the chunk bitmap
     *
     * @param bitmap the {@code short} that maps the chunk data
     * @return the current instance
     */
    public ChunkMetaBuilder bitmap(short bitmap) {
        this.bitmap = bitmap;

        return this;
    }

    @Override
    public void write(ByteBuf buf) {
        buf.writeInt(this.location.getX());
        buf.writeInt(this.location.getZ());

        buf.writeShort((int) this.bitmap);
    }
}
