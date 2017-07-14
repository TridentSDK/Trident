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
package net.tridentsdk.server.world.opt;

import lombok.Getter;
import lombok.Setter;
import net.tridentsdk.base.Vector;
import net.tridentsdk.doc.Debug;
import net.tridentsdk.meta.nbt.Tag;
import net.tridentsdk.server.packet.play.PlayOutDifficulty;
import net.tridentsdk.server.player.RecipientSelector;
import net.tridentsdk.server.world.TridentWorld;
import net.tridentsdk.world.opt.*;

import javax.annotation.concurrent.ThreadSafe;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicMarkableReference;

/**
 * Implementing class for handling the options in a world.
 */
@Getter
@ThreadSafe
public class WorldOptImpl implements WorldOpts {
    @Setter
    private volatile boolean allowFlight;
    @Setter
    private volatile boolean allowPvp = true;
    @Setter
    private volatile boolean allowPortals = true;
    @Setter
    private volatile boolean forceGameMode;

    @Debug("SURVIVAL")
    @Setter
    private volatile GameMode gameMode = GameMode.CREATIVE;
    private final AtomicMarkableReference<Difficulty> difficulty =
            new AtomicMarkableReference<>(Difficulty.NORMAL, false);
    private final Dimension dimension;
    @Setter
    private volatile int spawnProtectionRadius = 5;
    @Setter
    private volatile Vector spawn = new Vector(0, 64, 0);
    private final GameRuleMap gameRules = new GameRuleMap();

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
        this.dimension = spec.getDimension();

        if (!spec.isDefault()) {
            this.difficulty.set(spec.getDifficulty(), spec.isDifficultyLocked());
            this.gameMode = spec.getGameMode();
            spec.getGameRules().copyTo(this.gameRules);
            this.allowFlight = spec.isAllowFlight();
            this.allowPvp = spec.isAllowPvp();
            this.allowPortals = spec.isAllowPortals();
            this.forceGameMode = spec.isForceGameMode();
            this.spawnProtectionRadius = spec.getSpawnProtectionRadius();
            this.spawn = spec.getSpawn() == null ? this.randVector() : spec.getSpawn();
        }
    }

    /**
     * Creates a new set of world options which implements
     * those found in the compound file for this world.
     *
     * @param world the world to create options for
     * @param compound the compound to read data from
     */
    public WorldOptImpl(TridentWorld world, Tag.Compound compound) {
        this.world = world;
        this.dimension = Dimension.OVERWORLD;
    }

    /**
     * Generates a random vector.
     *
     * @return a random vector
     */
    private Vector randVector() {
        ThreadLocalRandom r = ThreadLocalRandom.current();
        int x = r.nextInt() % 1000;
        int z = r.nextInt() % 1000;
        return new Vector(x, this.world.getHighestY(x, z), z);
    }

    @Override
    public Difficulty getDifficulty() {
        return this.difficulty.getReference();
    }

    @Override
    public void setDifficulty(Difficulty difficulty) {
        Difficulty d0;
        do {
            d0 = this.getDifficulty();
        } while (!this.difficulty.isMarked() &&
                !this.difficulty.compareAndSet(d0, difficulty, false, false));

        if (d0 != difficulty) {
            RecipientSelector.inWorld(this.world, new PlayOutDifficulty(this.world));
        }
    }

    @Override
    public boolean isDifficultyLocked() {
        return this.difficulty.isMarked();
    }

    @Override
    public void setDifficultyLocked(boolean locked) {
        Difficulty difficulty;
        do {
            difficulty = this.getDifficulty();
        } while (!this.difficulty.attemptMark(difficulty, locked));
    }

    /**
     * Saves the world options as NBT data.
     */
    public void write(Tag.Compound compound) {
    }
}