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

/**
 * Entity abstraction base
 *
 * @author The TridentSDK Team
 */
public abstract class TridentEntity implements Entity {
    protected static final AtomicInteger counter = new AtomicInteger(-1);
    /**
     * The entity ID for the entity
     */
    protected final int id;
    /**
     * The identifier UUID for the entity
     */
    protected final UUID uniqueId;
    /**
     * The distance the entity has fallen
     */
    protected final AtomicLong fallDistance = new AtomicLong(0L);
    /**
     * The ticks that have passed since the entity was spawned, and alive
     */
    protected final AtomicLong ticksExisted = new AtomicLong(0L);
    /**
     * The movement vector for the entity
     */
    protected volatile Vector velocity;
    /**
     * Whether or not the movement vector has changed
     */
    protected volatile boolean velocityChanged;
    /**
     * The entity location
     */
    protected volatile Location loc;
    /**
     * Whether or not the entity has changed position
     */
    protected volatile boolean locationChanged;
    /**
     * Whether or not the entity is touching the ground
     */
    protected volatile boolean onGround;
    /**
     * The entity's passenger, if there are any
     */
    protected Entity passenger;
    /**
     * The name of the entity appearing above the head
     */
    protected String displayName;
    /**
     * Whether or not the name of the entity is visible
     */
    protected boolean nameVisible;
    /**
     * TODO
     */
    protected boolean silent;

    /**
     * Creates a new entity
     *
     * @param uniqueId      the UUID of the entity
     * @param spawnLocation the location which the entity is to be spawned
     */
    public TridentEntity(UUID uniqueId, Location spawnLocation) {
        this.uniqueId = uniqueId;
        this.id = TridentEntity.counter.incrementAndGet();

        this.velocity = new Vector(0.0D, 0.0D, 0.0D);
        this.velocityChanged = false;

        this.loc = spawnLocation;
        this.locationChanged = false;

        for (double y = this.loc.getY(); y > 0.0; y--) {
            Location l = new Location(this.loc.getWorld(), this.loc.getX(),
                    y, this.loc.getZ());

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
        // TODO Perhaps we should spawn it in a different method?
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
            Location l = new Location(this.loc.getWorld(), this.loc.getX(),
                    y, this.loc.getZ());

            if (l.getWorld().getBlockAt(l).getType() != Material.AIR) {
                this.fallDistance.set((long) (this.loc.getY() - y));
                this.onGround = this.fallDistance.get() == 0.0D;

                break;
            }
        }

        TridentPlayer.sendAll(new PacketPlayOutEntityTeleport().set("entityId", this.id)
                .set("location", this.loc)
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

    public void setLocation(Location loc) {
        this.loc = loc;
    }

    @Override
    public Vector getVelocity() {
        return this.velocity;
    }

    @Override
    public void setVelocity(Vector vector) {
        this.velocity = vector;
        this.velocityChanged = true;

        TridentPlayer.sendAll(new PacketPlayOutEntityVelocity().set("entityId", this.id)
                .set("velocity", vector));
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
