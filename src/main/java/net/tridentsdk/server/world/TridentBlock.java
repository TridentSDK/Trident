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

import com.google.common.collect.Lists;
import net.tridentsdk.base.Block;
import net.tridentsdk.base.Position;
import net.tridentsdk.base.Substance;
import net.tridentsdk.docs.InternalUseOnly;
import net.tridentsdk.meta.block.AbstractBlockMetaOwner;
import net.tridentsdk.meta.block.BlockMeta;
import net.tridentsdk.meta.component.MetaCollection;
import net.tridentsdk.meta.component.MetaFactory;
import net.tridentsdk.server.packets.play.out.PacketPlayOutBlockChange;
import net.tridentsdk.server.player.TridentPlayer;
import net.tridentsdk.util.Vector;

import java.util.Collections;
import java.util.List;

public class TridentBlock extends AbstractBlockMetaOwner<Block> implements Block {
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
    public void setSubstance(Substance substance) {
        setSubstanceAndMeta(substance, (byte) 0);
    }

    @Override
    public Position position() {
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
    public void setSubstanceAndMeta(Substance substance, byte data) {
        this.material = substance;
        this.data = data;

        TridentPlayer.sendAll(new PacketPlayOutBlockChange()
                .set("blockId", substance().id() << 4 | data)
                .set("location", location));

        ((TridentChunk) position().chunk()).setAt(location, substance, data, (byte) 255, (byte) 0);
    }

    @Override
    protected MetaCollection<Block> collect() {
        return MetaFactory.newCollection();
    }

    @Override
    public void clearMeta() {
        super.clearMeta();
        this.data = 0;
    }

    @Override
    public <M extends BlockMeta<Block>> boolean applyMeta(boolean replace, M... meta) {
        TridentChunk chunk = ((TridentChunk) location.chunk());
        Vector key = new Vector((int) location.x() & 15, location.y(), (int) location.z() & 15);
        List<BlockMeta> tiles = chunk.tilesInternal().computeIfAbsent(key, k -> Lists.newCopyOnWriteArrayList());
        Collections.addAll(tiles, meta);
        chunk.tilesInternal().put(key, tiles);

        return super.applyMeta(replace, meta);
    }
}
