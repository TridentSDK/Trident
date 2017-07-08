/*
 * Trident - A Multithreaded Server Alternative
 * Copyright 2017 The TridentSDK Team
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
package net.tridentsdk.server.world;

import lombok.Getter;
import net.tridentsdk.base.Block;
import net.tridentsdk.base.Position;
import net.tridentsdk.base.Substance;

import javax.annotation.concurrent.Immutable;

/**
 * Implementation of the Block class.
 */
@Immutable
public final class TridentBlock implements Block {
    /**
     * The position of the block, cached in a new object
     * such that changes to the original position will not
     * modify this one.
     */
    @Getter
    private final Position position;
    /**
     * The world containing this block
     */
    private final TridentWorld world;
    /**
     * The chunk X coordinate
     */
    private final int cX;
    /**
     * The chunk Z coordinate
     */
    private final int cZ;
    /**
     * The relative X coordinate
     */
    private final int relX;
    /**
     * The relative Y coordinate
     */
    private final int relY;
    /**
     * The relative Z coordinate
     */
    private final int relZ;

    /**
     * Creates a new block object at the given vector.
     *
     * @param position the vector at which the block is
     * located
     */
    public TridentBlock(Position position) {
        this.position = position;
        this.world = (TridentWorld) position.getWorld();
        this.cX = position.getIntX() >> 4;
        this.cZ = position.getIntZ() >> 4;
        this.relX = position.getIntX() & 15;
        this.relY = position.getIntY(); // Unfortunately cannot hoist Y due to breaking in set()
        this.relZ = position.getIntZ() & 15;
    }

    @Override
    public Substance getSubstance() {
        return Substance.fromNumericId(this.getChunk().get(this.relX, this.relY, this.relZ) >> 4);
    }

    @Override
    public void setSubstance(Substance substance) {
        TridentChunk chunk = this.getChunk();
        // set substance will need to reset the data because
        // retaining the data will usually not make sense
        // e.g. switching from colored wool to grass
        chunk.set(this.relX, this.relY, this.relZ, (short) (substance.getId() << 4));
    }

    @Override
    public byte getData() {
        return (byte) (this.getChunk().get(this.relX, this.relY, this.relZ) & 0xF);
    }

    @Override
    public void setData(byte data) {
        TridentChunk chunk = this.getChunk();
        // rshift needed to reset the lower bits
        int substanceId = chunk.get(this.relX, this.relY, this.relZ) >> 4;
        chunk.set(this.relX, this.relY, this.relZ, (short) (substanceId << 4 | data & 0xF));
    }

    /**
     * Obtains the chunk which the block is contained by.
     *
     * @return the container chunk
     */
    private TridentChunk getChunk() {
        return this.world.getChunkAt(this.cX, this.cZ);
    }
}