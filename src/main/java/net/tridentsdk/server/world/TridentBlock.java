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

package net.tridentsdk.server.world;

import net.tridentsdk.Coordinates;
import net.tridentsdk.base.Substance;
import net.tridentsdk.base.Block;
import net.tridentsdk.docs.InternalUseOnly;
import net.tridentsdk.util.Vector;

public class TridentBlock implements Block {
    private final Coordinates location;
    /**
     * The type for this block
     */
    protected volatile Substance material;
    /**
     * The block metadata
     */
    protected byte data;

    /**
     * Constructs the wrapper representing the block
     *
     * @param location Location of the Block
     */
    @InternalUseOnly
    public TridentBlock(Coordinates location) {
        this.location = location;

        // Note: Avoid recursion by not creating a new instance from World#tileAt(Location)
        Block worldBlock = location.world().tileAt(location);
        this.material = worldBlock.substance();
    }

    public TridentBlock(Coordinates location, Substance substance, byte meta) {
        this.location = location;
        this.material = substance;
        this.data = meta;
    }

    @Override
    public Substance substance() {
        return this.material;
    }

    @Override
    public void setSubstance(Substance material) {
        this.material = material;
    }

    @Override
    public Coordinates location() {
        return this.location;
    }

    @Override
    public byte meta() {
        return this.data;
    }

    @Override
    public void setMeta(byte data) {
        this.data = data;
    }

    @Override
    public Block relativeTile(Vector vector) {
        return new TridentBlock(this.location.relative(vector));
    }
}
