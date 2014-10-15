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

import net.tridentsdk.api.Difficulty;
import net.tridentsdk.api.GameMode;
import net.tridentsdk.api.Location;
import net.tridentsdk.api.entity.Entity;
import net.tridentsdk.api.entity.EntityProperties;
import net.tridentsdk.api.entity.Projectile;
import net.tridentsdk.api.entity.living.Player;
import net.tridentsdk.api.event.entity.EntityDamageEvent;
import net.tridentsdk.api.inventory.ItemStack;
import net.tridentsdk.api.threads.TaskExecutor;
import net.tridentsdk.api.world.Dimension;
import net.tridentsdk.api.world.LevelType;
import net.tridentsdk.entity.TridentInventoryHolder;
import net.tridentsdk.packets.play.out.PacketPlayOutChatMessage;
import net.tridentsdk.packets.play.out.PacketPlayOutDisconnect;
import net.tridentsdk.packets.play.out.PacketPlayOutJoinGame;
import net.tridentsdk.packets.play.out.PacketPlayOutKeepAlive;
import net.tridentsdk.server.TridentServer;
import net.tridentsdk.server.netty.ClientConnection;
import net.tridentsdk.server.netty.packet.Packet;

import java.util.Locale;
import java.util.UUID;

public class TridentPlayer extends TridentInventoryHolder implements Player {
    private static final TridentPlayer[] players = { };

    private final PlayerConnection connection;
    private final TaskExecutor executor = TridentServer.getInstance().provideThreads().providePlayerThread(this);
    private volatile Locale locale;
    private volatile float flyingSpeed;
    private volatile short heldSlot;

    public TridentPlayer(UUID uniqueId, Location spawnLocation, ClientConnection connection) {
        super(uniqueId, spawnLocation);

        this.connection = PlayerConnection.createPlayerConnection(connection, this);
        this.flyingSpeed = 1.0F;
    }

    public static void sendAll(Packet packet) {
        for (TridentPlayer p : TridentPlayer.players) {
            p.connection.sendPacket(packet);
        }
    }

    public static Player spawnPlayer(ClientConnection connection, UUID id) {
        // TODO: find player's spawn location
        TridentPlayer p = new TridentPlayer(id, new Location(null, 0.0, 0.0, 0.0), connection);

        p.connection.sendPacket(new PacketPlayOutJoinGame().set("entityId", p.getId())
                .set("gamemode", GameMode.SURVIVAL)
                .set("dimension", Dimension.OVERWORLD)
                .set("difficulty", Difficulty.NORMAL)
                .set("maxPlayers", (short) 10)
                .set("levelType",
                        LevelType.DEFAULT)); // code to test if client will
        // move on

        return p;
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
                if (slot > 8 || slot < 0) {
                    throw new IllegalArgumentException("Slot must be within the ranges of 0-8");
                }

                TridentPlayer.this.heldSlot = slot;
            }
        });
    }

    @Override
    public ItemStack getItemInHand() {
        return this.getInventory().getContents()[(int) this.heldSlot + 36];
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

    @Override
    public float getFlyingSpeed() {
        return this.flyingSpeed;
    }

    @Override
    public void setFlyingSpeed(float flyingSpeed) {
        this.flyingSpeed = flyingSpeed;
    }

    @Override
    public Locale getLocale() {
        return this.locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    @Override
    public void hide(Entity entity) {
        // TODO
    }

    @Override
    public void show(Entity entity) {
        // TODO
    }

    @Override
    public boolean isNameVisible() {
        return true;
    }

    @Override
    public void applyProperties(EntityProperties properties) {
        // TODO
    }

    @Override
    public <T extends Projectile> T launchProjectile(EntityProperties properties) {
        // TODO
        return null;
    }

    @Override
    public EntityDamageEvent getLastDamageCause() {
        return null;
    }

    @Override
    public Player hurtByPlayer() {
        return null;
    }

    /* (non-Javadoc)
     * @see net.tridentsdk.api.entity.living.Player#getGameMode()
     */
    @Override
    public GameMode getGameMode() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see net.tridentsdk.api.entity.living.Player#getMoveSpeed()
     */
    @Override
    public float getMoveSpeed() {
        // TODO Auto-generated method stub
        return 0.0F;
    }

    /* (non-Javadoc)
     * @see net.tridentsdk.api.entity.living.Player#setMoveSpeed(float)
     */
    @Override
    public void setMoveSpeed(float speed) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see net.tridentsdk.api.entity.living.Player#getSneakSpeed()
     */
    @Override
    public float getSneakSpeed() {
        // TODO Auto-generated method stub
        return 0.0F;
    }

    /* (non-Javadoc)
     * @see net.tridentsdk.api.entity.living.Player#setSneakSpeed(float)
     */
    @Override
    public void setSneakSpeed(float speed) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see net.tridentsdk.api.entity.living.Player#getWalkSpeed()
     */
    @Override
    public float getWalkSpeed() {
        // TODO Auto-generated method stub
        return 0.0F;
    }

    /* (non-Javadoc)
     * @see net.tridentsdk.api.entity.living.Player#setWalkSpeed(float)
     */
    @Override
    public void setWalkSpeed(float speed) {
        // TODO Auto-generated method stub

    }
}
