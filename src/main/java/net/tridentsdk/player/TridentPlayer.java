/*
 *     Trident - A Multithreaded Server Alternative
 *     Copyright (C) 2014, The TridentSDK Team
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.tridentsdk.player;

import net.tridentsdk.api.entity.living.Player;
import net.tridentsdk.api.nbt.CompoundTag;
import net.tridentsdk.api.threads.TaskExecutor;
import net.tridentsdk.api.world.LevelType;
import net.tridentsdk.packets.play.out.*;
import net.tridentsdk.server.TridentServer;
import net.tridentsdk.server.netty.ClientConnection;
import net.tridentsdk.server.netty.packet.Packet;
import net.tridentsdk.world.TridentChunk;
import net.tridentsdk.world.TridentWorld;

import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListSet;

public class TridentPlayer extends OfflinePlayer {
    private static final Set<TridentPlayer> players = new ConcurrentSkipListSet<>(); // TODO: Check if best choice for Players

    private final PlayerConnection connection;
    private final TaskExecutor executor = TridentServer.getInstance().provideThreads().providePlayerThread(this);
    private volatile Locale locale;

    public TridentPlayer(CompoundTag tag, TridentWorld world, ClientConnection connection) {
        super(tag, world);

        this.connection = PlayerConnection.createPlayerConnection(connection, this);
    }

    public static void sendAll(Packet packet) {
        for (TridentPlayer p : players) {
            p.connection.sendPacket(packet);
        }
    }

    public static Player spawnPlayer(ClientConnection connection, UUID id) {
        OfflinePlayer offlinePlayer = OfflinePlayer.getOfflinePlayer(id);

        TridentPlayer p = new TridentPlayer(offlinePlayer.toNbt(),
                (TridentWorld) offlinePlayer.getWorld(), connection);

        p.connection.sendPacket(new PacketPlayOutJoinGame().set("entityId", p.getId())
                .set("gamemode", p.getGameMode())
                .set("dimension", p.getWorld().getDimesion())
                .set("difficulty", p.getWorld().getDifficulty())
                .set("maxPlayers", (short) 10)
                .set("levelType",
                        LevelType.DEFAULT));

        p.connection.sendPacket(new PacketPlayOutSpawnPosition().set("location", p.getSpawnLocation()));
        p.connection.sendPacket(p.abilities.toPacket());
        p.connection.sendPacket(new PacketPlayOutPlayerCompleteMove().set("location", p.getLocation())
                .set("flags", (byte) 0));

        p.sendChunks(7);


        players.add(p);

        return p;
    }

    public static TridentPlayer getPlayer(UUID id) {
        for (TridentPlayer player : players) {
            if (player.getUniqueId().equals(id)) {
                return player;
            }
        }

        return null;
    }

    @Override
    public void tick() {
        this.executor.addTask(new Runnable() {
            @Override
            public void run() {
                TridentPlayer.super.tick();

                if (TridentPlayer.this.connection.getKeepAliveId() == -1) {
                    // send Keep Alive packet if not sent already
                    PacketPlayOutKeepAlive packet = new PacketPlayOutKeepAlive();

                    TridentPlayer.this.connection.sendPacket(packet);
                    TridentPlayer.this.connection.setKeepAliveId(packet.getKeepAliveId(),
                            TridentPlayer.this.ticksExisted.get());
                } else if (TridentPlayer.this.ticksExisted.get() -
                        TridentPlayer.this.connection.getKeepAliveSent() >= 600L) {
                    // kick the player for not responding to the keep alive within 30 seconds/600 ticks
                    TridentPlayer.this.kickPlayer("Timed out!");
                }
            }
        });
    }

    /*
     * @NotJavaDoc
     * TODO: Create Message API and utilize it
     */
    public void kickPlayer(final String reason) {
        this.executor.addTask(new Runnable() {
            @Override
            public void run() {
                TridentPlayer.this.connection.sendPacket(new PacketPlayOutDisconnect().set("reason", reason));
                TridentPlayer.this.connection.logout();
            }
        });
    }

    public PlayerConnection getConnection() {
        return this.connection;
    }

    public void setSlot(final short slot) {
        this.executor.addTask(new Runnable() {
            @Override
            public void run() {
                if ((int) slot > 8 || (int) slot < 0) {
                    throw new IllegalArgumentException("Slot must be within the ranges of 0-8");
                }

                TridentPlayer.super.selectedSlot = slot;
            }
        });
    }

    @Override
    public void sendMessage(final String... messages) {
        // TODO: Verify proper implementation
        this.executor.addTask(new Runnable() {
            @Override
            public void run() {
                for (String message : messages) {
                    if (message != null) {
                        TridentPlayer.this.connection.sendPacket(new PacketPlayOutChatMessage().set("jsonMessage",
                                message)
                                .set("position", PacketPlayOutChatMessage.ChatPosition.CHAT));
                    }
                }
            }
        });
    }

    private void sendChunks(int viewDistance) {
        int centX = ((int) Math.floor(loc.getX())) >> 4;
        int centZ = ((int) Math.floor(loc.getZ())) >> 4;

        for (int x = (centX - viewDistance); x <= (centX + viewDistance); x += 1) {
            for (int z = (centZ - viewDistance); z <= (centZ + viewDistance); z += 1) {
                // TODO: store known chunks for less redundant packets

                PacketPlayOutChunkData packet = new PacketPlayOutChunkData(); // TODO: Use ChunkBulk for efficiency

                ((TridentChunk) getWorld().getChunkAt(x, z, false)).write(packet);

                connection.sendPacket(packet);
            }
        }
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }
}
