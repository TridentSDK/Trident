/*
 * Copyright (c) 2014, The TridentSDK Team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     1. Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *     2. Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *     3. Neither the name of the The TridentSDK Team nor the
 *        names of its contributors may be used to endorse or promote products
 *        derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL The TridentSDK Team BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.tridentsdk.impl;

import net.tridentsdk.api.Block;
import net.tridentsdk.api.Location;
import net.tridentsdk.api.entity.Entity;
import net.tridentsdk.api.event.ManagerList;
import net.tridentsdk.api.inventory.Inventory;
import net.tridentsdk.api.inventory.ItemStack;
import net.tridentsdk.api.trade.ItemPair;
import net.tridentsdk.api.util.TridentLogger;
import net.tridentsdk.api.util.Vector;
import net.tridentsdk.api.world.*;
import net.tridentsdk.world.TridentWorldLoader;

import java.util.logging.Logger;

/**
 * Factory creation methods for basic API classes
 *
 * @author The TridentSDK Team
 */
public final class TridentFactory {
    private TridentFactory() {}

    /* Block */

    public static Block createBlock() {
        return new Block(null);
    }

    public static Block createBlock(Location location) {
        return new Block(location);
    }

    public static Block createBlock(World world, double x, double y, double z) {
        return new Block(TridentFactory.createLocation(world, x, y, z));
    }

    /* Location */

    public static Location createLocation() {
        return new Location(null, 0.0, 0.0, 0.0);
        // TODO make default world, or stay null?
    }

    public static Location createLocation(World world, double x, double y, double z) {
        return new Location(world, x, y, z);
    }

    public static Location createLocation(World world, double x, double y, double z, float yaw, float pitch) {
        return new Location(world, x, y, z, yaw, pitch);
    }

    public static Location createLocation(Location location, float yaw, float pitch) {
        return new Location(location.getWorld(), location.getX(), location.getY(), location.getZ(), yaw, pitch);
    }

    /* World */

    public static ChunkLocation createChunkLoc() {
        return new ChunkLocation(0, 0);
    }

    public static ChunkLocation createChunkLoc(int x, int z) {
        return new ChunkLocation(x, z);
    }

    public static ChunkLocation createChunkLoc(ChunkLocation chunkLocation) {
        return new ChunkLocation(chunkLocation);
    }

    // Not thread safe
    public static WorldLoader createWorldLoader() {
        return new TridentWorldLoader() {
            private World world;

            @Override public World load(String world) {
                this.world = super.load(world);
                return this.world;
            }

            @Override public boolean chunkExists(World world, ChunkLocation location) {
                return world.getChunkAt(location, false) != null;
            }

            @Override public Chunk loadChunk(World world, int x, int z) {
                return world.getChunkAt(TridentFactory.createChunkLoc(x, z), true);
            }

            @Override public Chunk loadChunk(World world, ChunkLocation location) {
                return world.getChunkAt(location, true);
            }

            @Override public void saveChunk(Chunk chunk) {
                // TODO
            }
        };
    }

    public static World createWorld(String name) {
        return TridentFactory.createWorldLoader().load(name);
    }

    /* Utils */

    public static Vector createVector() {
        return new Vector(0, 0, 0);
    }

    public static Vector createVector(double x, double y, double z) {
        return new Vector(x, y, z);
    }

    public static Vector createVector(int x, int y, int z) {
        return new Vector(x, y, z);
    }

    public static Logger createLogger() {
        return new TridentLogger();
    }

    /* Inventory */

    public static ItemStack createItemStack() {
        return new ItemStack();
    }

    public static Inventory createInventory() {
        return new Inventory() {
            @Override public ItemStack[] getContents() {
                return new ItemStack[0]; // TODO
            }
        };
    }

    /* Trade */

    public static ItemPair createItemPair() {
        return new ItemPair(null, null);
    }

    public static ItemPair createItemPair(ItemStack itemStack) {
        return new ItemPair(itemStack);
    }

    public static ItemPair createItemPair(ItemStack itemStack, ItemStack itemStack0) {
        return new ItemPair(itemStack, itemStack0);
    }

    /* No provided NBT or todo */

    /* Event */

    public static ManagerList createManagerList() {
        return new ManagerList();
    }

    /* Entity */

    public static Entity createEntity(World world, Class<? extends Entity> entity) {
        // TODO
        return null;
    }



    /* Board not ready */

    /* Possibly create packets */
}
