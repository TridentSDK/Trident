/*
 * Trident - A Multithreaded Server Alternative
 * Copyright 2016 The TridentSDK Team
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
package net.tridentsdk.server.world.opt;

import net.tridentsdk.base.Vector;
import net.tridentsdk.doc.Internal;
import net.tridentsdk.server.world.TridentWorld;
import net.tridentsdk.world.opt.*;

import javax.annotation.concurrent.ThreadSafe;

/**
 * Implementing class for handling the options in a world.
 */
@ThreadSafe
public class WorldOptImpl implements WorldOpts {
    // TODO appropriate packets
    private volatile boolean allowFlight = false;
    private volatile boolean allowPvp = true;
    private volatile boolean allowPortals = true;
    private volatile boolean forceGamemode = false;

    private volatile GameMode gameMode = GameMode.CREATIVE; // TODO Remove once in production
    private volatile Difficulty difficulty = Difficulty.NORMAL;
    private volatile Dimension dimension = Dimension.OVERWORLD;
    private volatile boolean difficultyLocked = false;
    private volatile int spawnProtection = 5;
    private volatile Vector spawn = new Vector();
    private final GameRuleMap map = new GameRuleMap();

    private final TridentWorld world;

    /**
     * Creates a new set of world options for the given
     * world and the given creation options passed in by
     * a plugin.
     *
     * @param world the world possessing these options.
     */
    public WorldOptImpl(TridentWorld world, WorldCreateSpec spec) {
        this.world = world;

        if (!spec.isDefault()) {
        }
    }

    @Override
    public boolean allowFlight() {
        return this.allowFlight;
    }

    @Override
    public void setAllowFlight(boolean allow) {
        this.allowFlight = allow;
    }

    @Override
    public boolean allowPvp() {
        return this.allowPvp;
    }

    @Override
    public void setAllowPvp(boolean allow) {
        this.allowPvp = allow;
    }

    @Override
    public boolean allowPortals() {
        return this.allowPortals;
    }

    @Override
    public void setAllowPortals(boolean allow) {
        this.allowPortals = allow;
    }

    @Override
    public boolean forceGameMode() {
        return this.forceGamemode;
    }

    @Override
    public void setForceGameMode(boolean force) {
        this.forceGamemode = force;
    }

    @Override
    public GameMode gameMode() {
        return this.gameMode;
    }

    @Override
    public void setGameMode(GameMode mode) {
        this.gameMode = mode;
    }

    @Override
    public Difficulty difficulty() {
        return this.difficulty;
    }

    @Override
    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    @Override
    public boolean difficultyLocked() {
        return this.difficultyLocked;
    }

    @Override
    public void setDifficultyLocked(boolean locked) {
        this.difficultyLocked = locked;
    }

    @Override
    public int spawnProtectionRadius() {
        return this.spawnProtection;
    }

    @Override
    public void setSpawnProtectionRadius(int radius) {
        this.spawnProtection = radius;
    }

    @Override
    public Dimension dimension() {
        return this.dimension;
    }

    /**
     * Internal method for setting the dimension during
     * world customization. DO NOT USE. This needs an
     * annotation @INTERNALINTERNAL.
     */
    @Internal
    public void setDimension(Dimension dimension) {
        this.dimension = dimension;
    }

    @Override
    public Vector spawn() {
        return this.spawn;
    }

    @Override
    public void setSpawn(Vector vector) {
        this.spawn = vector;
    }

    @Override
    public GameRuleMap gameRules() {
        return this.map;
    }

    /**
     * Loads the world options from the NBT data.
     */
    public void load() {
    }

    /**
     * Saves the world options as NBT data.
     */
    public void save() {
    }
}