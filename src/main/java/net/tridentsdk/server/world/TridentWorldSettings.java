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

import net.tridentsdk.server.packets.play.out.PacketPlayOutGameStateChange;
import net.tridentsdk.server.packets.play.out.PacketPlayOutServerDifficulty;
import net.tridentsdk.server.player.TridentPlayer;
import net.tridentsdk.world.settings.*;

import java.util.Set;

/**
 * Represents changable and unchangable attributes of a world
 *
 * @author The TridentSDK Team
 */
public class TridentWorldSettings implements WorldSettings {

    private LevelType levelType = LevelType.DEFAULT;
    private long seed = 0;
    private Dimension dimension = Dimension.OVERWORLD;
    private Difficulty difficulty = Difficulty.EASY;

    public static WorldSettings load(TridentWorld world, WorldCreateOptions options) {
        return new TridentWorldSettings();
    }

    @Override
    public GameMode defaultGameMode() {
        return null;
    }

    @Override
    public void setGameMode(GameMode gameMode) {
        // this.defaultGamemode = gameMode;

        PacketPlayOutGameStateChange change = new PacketPlayOutGameStateChange();
        change.set("reason", 3).set("value", (float) gameMode.asByte());
        TridentPlayer.sendFiltered(change, p -> p.world().equals(this));
    }

    @Override
    public LevelType levelType() {
        return levelType;
    }

    @Override
    public long seed() {
        return seed;
    }

    @Override
    public Dimension dimension() {
        return dimension;
    }

    @Override
    public Difficulty difficulty() {
        return difficulty;
    }

    @Override
    public void setDifficulty(Difficulty difficulty) {
        // this.difficulty = difficulty;

        PacketPlayOutServerDifficulty d = new PacketPlayOutServerDifficulty();
        d.set("difficulty", d);
        TridentPlayer.sendFiltered(d, p -> p.world().equals(this));
    }

    @Override
    public boolean isRule(String rule) {
        return false;
    }

    @Override
    public Set<String> gameRules() {
        return null;
    }

    @Override
    public boolean allowPvp() {
        return false;
    }

    @Override
    public void setAllowPvp(boolean enable) {

    }

    @Override
    public boolean generateStructures() {
        return false;
    }
}