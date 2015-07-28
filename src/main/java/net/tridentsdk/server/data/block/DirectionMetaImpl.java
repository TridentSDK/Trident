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
package net.tridentsdk.server.data.block;

import net.tridentsdk.base.Block;
import net.tridentsdk.base.BlockDirection;
import net.tridentsdk.base.Substance;
import net.tridentsdk.meta.block.DirectionMeta;
import net.tridentsdk.meta.component.Meta;
import net.tridentsdk.meta.component.MetaCollection;

import static net.tridentsdk.base.Substance.*;

/**
 * Represents a block direction
 *
 * @author The TridentSDK Team
 */
// TODO
public class DirectionMetaImpl implements DirectionMeta {
    private volatile BlockDirection direction = BlockDirection.SELF;

    @Override
    public BlockDirection direction() {
        return direction;
    }

    @Override
    public void setDirection(BlockDirection direction) {
        this.direction = direction;
    }

    @Override
    public byte encode() {
        return 0;
    }

    @Override
    public Meta<Block> decode(Block instance, float yaw, byte direction, byte cx, byte cy, byte cz, short damageValue) {
        DirectionMeta meta = new DirectionMetaImpl();

        return meta;
    }

    @Override
    public Meta<Block> make() {
        return new DirectionMetaImpl();
    }

    @Override
    public Substance[] applyTo(MetaCollection collection) {
        collection.putIfAbsent(DirectionMeta.class, this);
        return new Substance[]{TORCH, REDSTONE_TORCH_ON, REDSTONE_TORCH_OFF, RAILS, ACTIVATOR_RAIL, DETECTOR_RAIL,
                POWERED_RAIL, ACACIA_STAIRS, BIRCH_WOOD_STAIRS, BRICK_STAIRS, COBBLESTONE_STAIRS, DARK_OAK_STAIRS,
                JUNGLE_WOOD_STAIRS, NETHER_BRICK_STAIRS, QUARTZ_STAIRS, RED_SANDSTONE_STAIRS, SANDSTONE_STAIRS,
                SMOOTH_STAIRS, SPRUCE_WOOD_STAIRS, WOOD_STAIRS, LEVER, ACACIA_DOOR, BIRCH_DOOR, DARK_OAK_DOOR,
                IRON_DOOR, IRON_TRAP_DOOR, JUNGLE_DOOR, SPRUCE_DOOR, SPRUCE_DOOR, TRAP_DOOR, WOOD_DOOR, WOODEN_DOOR,
                IRON_DOOR_BLOCK, STONE_BUTTON, WOOD_BUTTON, SIGN_POST, SIGN, LADDER, WALL_SIGN, FURNACE,
                BURNING_FURNACE, CHEST, ENDER_CHEST, TRAPPED_CHEST, /* Double chest */ DISPENSER, DROPPER, HOPPER,
                PUMPKIN, JACK_O_LANTERN, BED_BLOCK, BED /* ? */, REDSTONE_COMPARATOR, REDSTONE_COMPARATOR_OFF,
                REDSTONE_COMPARATOR_ON /* ? */ /* Repeater */, PISTON_BASE, PISTON_EXTENSION, PISTON_MOVING_PIECE,
                PISTON_STICKY_BASE, ENDER_PORTAL_FRAME};
    }
}
