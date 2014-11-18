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
package net.tridentsdk.api.world;

import java.io.Serializable;

/**
 * Stores the location of a Chunk
 *
 * @author drew
 */
public class ChunkLocation implements Serializable, Cloneable {
    private static final long serialVersionUID = 9083698035337137603L;
    private int x;
    private int z;

    public ChunkLocation(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public ChunkLocation(ChunkLocation coord) {
        this.x = coord.getX();
        this.z = coord.getZ();
    }

    public int getX() {
        return this.x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getZ() {
        return this.z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public Chunk getChunk() {
        return null;
    }
}
