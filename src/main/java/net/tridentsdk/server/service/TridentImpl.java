package net.tridentsdk.server.service;

import com.google.common.collect.ForwardingCollection;
import com.google.common.collect.ImmutableMap;
import net.tridentsdk.concurrent.Scheduler;
import net.tridentsdk.concurrent.SelectableThreadPool;
import net.tridentsdk.entity.living.Player;
import net.tridentsdk.event.Events;
import net.tridentsdk.inventory.Inventories;
import net.tridentsdk.plugin.Plugins;
import net.tridentsdk.plugin.channel.PluginChannels;
import net.tridentsdk.registry.Implementation;
import net.tridentsdk.registry.PlayerStatus;
import net.tridentsdk.registry.Players;
import net.tridentsdk.server.TridentTaskScheduler;
import net.tridentsdk.server.concurrent.ConcurrentTaskExecutor;
import net.tridentsdk.server.event.EventHandler;
import net.tridentsdk.server.packets.play.out.PacketPlayOutPluginMessage;
import net.tridentsdk.server.player.OfflinePlayer;
import net.tridentsdk.server.player.TridentPlayer;
import net.tridentsdk.server.plugin.PluginHandler;
import net.tridentsdk.server.window.TridentInventories;
import net.tridentsdk.server.world.TridentWorldLoader;
import net.tridentsdk.server.world.change.DefaultMassChange;
import net.tridentsdk.world.MassChange;
import net.tridentsdk.world.World;
import net.tridentsdk.world.WorldLoader;
import net.tridentsdk.world.gen.AbstractGenerator;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

/**
 * Entry point for plugin API implementations
 *
 * @author The TridentSDK Team
 */
public class TridentImpl implements Implementation {
    private final Scheduler scheduler = TridentTaskScheduler.create();
    private final PluginChannels channelHandler = new PluginChannels() {
        @Override
        public void sendPluginMessage(String channel, byte... data) {
            TridentPlayer.sendAll(new PacketPlayOutPluginMessage().set("channel", channel).set("data", data));
        }
    };
    private final Inventories windowHandler = new TridentInventories();
    private final Events events = EventHandler.create();
    private final PlayerStatus status = new Statuses();
    private final Plugins plugins = new PluginHandler();

    class PlayersImpl extends ForwardingCollection<Player> implements Players {
        @Override
        protected Collection<Player> delegate() {
            return TridentPlayer.players();
        }

        @Override
        public Player fromUuid(UUID uuid) {
            return TridentPlayer.getPlayer(uuid);
        }

        @Override
        public Player offline(UUID uuid) {
            return OfflinePlayer.getOfflinePlayer(uuid);
        }
    }

    private final Players players = new PlayersImpl();

    @Override
    public SelectableThreadPool newPool(int i, String s) {
        return ConcurrentTaskExecutor.create(i, s);
    }

    @Override
    public WorldLoader newLoader(Class<? extends AbstractGenerator> g) {
        if (g == null) {
            return new TridentWorldLoader();
        }

        return new TridentWorldLoader(g);
    }

    @Override
    public MassChange newMc(World world) {
        return new DefaultMassChange(world);
    }

    @Override
    public Map<String, World> worlds() {
        return ImmutableMap.copyOf(TridentWorldLoader.WORLDS);
    }

    @Override
    public Players players() {
        return players;
    }

    @Override
    public PlayerStatus statuses() {
        return status;
    }

    @Override
    public Events events() {
        return events;
    }

    @Override
    public Plugins plugins() {
        return plugins;
    }

    @Override
    public Scheduler scheduler() {
        return scheduler;
    }

    @Override
    public PluginChannels channels() {
        return channelHandler;
    }

    @Override
    public Inventories inventories() {
        return windowHandler;
    }
}
