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

package net.tridentsdk.entity;

import net.tridentsdk.api.Location;
import net.tridentsdk.api.Material;
import net.tridentsdk.api.entity.Entity;
import net.tridentsdk.api.entity.EntityType;
import net.tridentsdk.api.util.Vector;
import net.tridentsdk.api.world.World;
import net.tridentsdk.packets.play.out.PacketPlayOutEntityTeleport;
import net.tridentsdk.packets.play.out.PacketPlayOutEntityVelocity;
import net.tridentsdk.player.TridentPlayer;
import net.tridentsdk.server.TridentServer;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public abstract class TridentEntity implements Entity {
    protected final AtomicInteger counter = new AtomicInteger(-1);
    protected final int id;
    protected final UUID uniqueId;
    protected final AtomicLong fallDistance = new AtomicLong(0L);
    protected final AtomicLong ticksExisted = new AtomicLong(0L);
    protected volatile Vector velocity;
    protected volatile boolean velocityChanged;
    protected volatile Location loc;
    protected volatile boolean locationChanged;
    protected volatile boolean onGround;
    protected Entity passenger;
    protected String displayName;
    protected boolean nameVisible;
    protected boolean silent;

    public TridentEntity(UUID uniqueId, Location spawnLocation) {
        this.uniqueId = uniqueId;
        this.id = this.counter.addAndGet(1);

        this.velocity = new Vector(0.0D, 0.0D, 0.0D);
        this.velocityChanged = false;

        this.loc = spawnLocation;
        this.locationChanged = false;

        for (double y = this.loc.getY(); y > 0.0; y--) {
            Location l = new Location(this.loc.getWorld(), this.loc.getX(), y, this.loc.getZ());

            if (l.getWorld().getBlockAt(l).getType() != Material.AIR) {
                this.fallDistance.set((long) (this.loc.getY() - y));
                this.onGround = this.fallDistance.get() == 0.0D;
                // Depending on what you want to do here, it may or may not work when multithreading
                // TODO

                break;
            }
        }

        this.passenger = null;

        TridentServer.getInstance().getEntityManager().registerEntity(this);
    }

    @Override
    public void teleport(double x, double y, double z) {
        this.teleport(new Location(this.getWorld(), x, y, z));
    }

    @Override
    public void teleport(Entity entity) {
        this.teleport(entity.getLocation());
    }

    @Override
    public void teleport(Location location) {
        this.loc = location;
        this.locationChanged = true;

        for (double y = this.loc.getY(); y > 0.0; y--) {
            Location l = new Location(this.loc.getWorld(), this.loc.getX(), y, this.loc.getZ());

            if (l.getWorld().getBlockAt(l).getType() != Material.AIR) {
                this.fallDistance.set((long) (this.loc.getY() - y));
                this.onGround = this.fallDistance.get() == 0.0D;

                break;
            }
        }

        TridentPlayer.sendAll(new PacketPlayOutEntityTeleport().set("entityId", this.id).set("location", this.loc)
                .set("onGround", this.onGround));
    }

    @Override
    public World getWorld() {
        return this.loc.getWorld();
    }

    @Override
    public Location getLocation() {
        return this.loc;
    }

    @Override
    public Vector getVelocity() {
        return this.velocity;
    }

    @Override
    public void setVelocity(Vector vector) {
        this.velocity = vector;
        this.velocityChanged = true;

        TridentPlayer.sendAll(new PacketPlayOutEntityVelocity().set("entityId", this.id).set("velocity", vector));
    }

    @Override
    public String getDisplayName() {
        return this.displayName;
    }

    @Override
    public void setDisplayName(String name) {
        this.displayName = name;
    }

    @Override
    public boolean isSilent() {
        return this.silent;
    }

    @Override
    public UUID getUniqueId() {
        return this.uniqueId;
    }

    @Override
    public void tick() {
        this.ticksExisted.incrementAndGet();
    }

    @Override
    public boolean isOnGround() {
        return this.onGround;
    }

    /**
     * TODO
     *
     * @param radius the spherical radius to look for entities around
     */
    @Override
    public List<Entity> getNearbyEntities(double radius) {
        return null;
    }

    @Override
    public int getId() {
        return this.id;
    }

    /**
     * TODO
     */
    @Override
    public void remove() {
    }

    @Override
    public Entity getPassenger() {
        return this.passenger;
    }

    @Override
    public void setPassenger(Entity entity) {
        this.passenger = entity;

        // TODO: Update clients
    }

    @Override
    public void eject() {
        //
    }

    @Override
    public EntityType getType() {
        return null;
    }
}
