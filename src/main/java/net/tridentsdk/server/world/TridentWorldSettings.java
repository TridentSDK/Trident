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
        return null;
    }

    @Override
    public long seed() {
        return 0;
    }

    @Override
    public Dimension dimension() {
        return null;
    }

    @Override
    public Difficulty difficulty() {
        return null;
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