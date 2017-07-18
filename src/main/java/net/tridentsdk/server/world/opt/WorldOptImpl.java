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
    private final TridentWorld world;

    @Debug("SURVIVAL")
    @Setter
    private volatile GameMode gameMode = GameMode.CREATIVE;
    private final AtomicMarkableReference<Difficulty> difficulty =
            new AtomicMarkableReference<>(Difficulty.NORMAL, false);
    @Setter
    private volatile Vector spawn;
    private final GameRuleMap gameRules = new GameRuleMap();

    /**
     * Creates a new set of world options for the given
     * world and the given creation options passed in by
     * a plugin.
     *
     * @param world the world possessing these options.
     * @param spec the world spec
     */
    public WorldOptImpl(TridentWorld world, WorldCreateSpec spec) {
        this.world = world;

        if (!spec.isDefault()) {
            this.difficulty.set(spec.getDifficulty(), spec.isDifficultyLocked());
            this.gameMode = spec.getGameMode();
            spec.getGameRules().copyTo(this.gameRules);
            this.spawn = spec.getSpawn() == null ? this.randVector() : spec.getSpawn();
        } else {
            this.spawn = this.randVector();
        }
    }

    /**
     * Creates a new set of world options which implements
     * those found in the compound file for this world.
     *
     * @param world the world to create options for
     * @param compound the compound to read data from
     */
    @Debug("creative")
    public WorldOptImpl(TridentWorld world, Tag.Compound compound) {
        this.world = world;

        this.gameMode = GameMode.CREATIVE; // GameMode.from(compound.getInt("GameType"));
        this.difficulty.set(Difficulty.from(compound.getByte("Difficulty")),
                compound.getByte("DifficultyLocked") == 1);
        this.spawn = new Vector(compound.getInt("SpawnX"), compound.getInt("SpawnY"), compound.getInt("SpawnZ"));
        Tag.Compound rulesCmp = compound.getCompound("GameRules");
        for (String s : rulesCmp.getEntries().keySet()) {
            GameRule<Object> rule = GameRule.from(s);
            this.gameRules.set(rule, rule.parseValue(rulesCmp.getString(s)));
        }
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
        return new Vector(x, this.world.getHighestY(x, z) + 1, z);
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
     *
     * @param compound the compound to write to
     */
    public void write(Tag.Compound compound) {
        compound.putInt("GameType", this.gameMode.asInt());
        compound.putByte("Difficulty", this.difficulty.getReference().asByte());
        compound.putByte("DifficultyLocked", (byte) (this.difficulty.isMarked() ? 1 : 0));

        Vector spawn = this.spawn;
        compound.putInt("SpawnX", spawn.getIntX());
        compound.putInt("SpawnY", spawn.getIntY());
        compound.putInt("SpawnZ", spawn.getIntZ());

        Tag.Compound rulesCmp = new Tag.Compound("GameRules");
        for (String s : GameRule.getKeyStrings()) {
            rulesCmp.putString(s, String.valueOf(this.gameRules.<Object>get(GameRule.from(s))));
        }
        compound.putCompound(rulesCmp);
    }
}