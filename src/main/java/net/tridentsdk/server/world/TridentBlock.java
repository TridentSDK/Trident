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

import net.tridentsdk.base.Block;
import net.tridentsdk.base.Position;
import net.tridentsdk.base.Substance;
import net.tridentsdk.docs.InternalUseOnly;
import net.tridentsdk.server.packets.play.out.PacketPlayOutBlockChange;
import net.tridentsdk.server.player.TridentPlayer;
import net.tridentsdk.util.Vector;

public class TridentBlock implements Block {
    private final Position location;
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
    public TridentBlock(Position location) {
        this.location = location;

        // Note: Avoid recursion by not creating a new instance from World#blockAt(Location)
        Block worldBlock = location.world().blockAt(location);
        this.material = worldBlock.substance();
    }

    public TridentBlock(Position location, Substance substance, byte meta) {
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
        setSubstanceAndMeta(material, (byte) 0);
    }

    @Override
    public Position location() {
        return this.location;
    }

    @Override
    public byte meta() {
        return this.data;
    }

    @Override
    public void setMeta(byte data) {
        setSubstanceAndMeta(this.material, data);
    }

    @Override
    public Block relativeBlock(Vector vector) {
        return new TridentBlock(this.location.relative(vector));
    }

    @Override
    public void setSubstanceAndMeta(Substance material, byte data) {
        this.material = material;
        this.data = data;


        TridentPlayer.sendAll(new PacketPlayOutBlockChange()
                .set("blockId", substance().id() << 4 | data)
                .set("location", location));

        ((TridentChunk) location().chunk()).setAt(location, material, data, (byte) 255, (byte) 0);
    }
}
