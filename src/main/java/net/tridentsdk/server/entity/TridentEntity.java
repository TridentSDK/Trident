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
package net.tridentsdk.server.entity;

import net.tridentsdk.api.Location;
import net.tridentsdk.api.Material;
import net.tridentsdk.api.entity.Entity;
import net.tridentsdk.api.entity.EntityProperties;
import net.tridentsdk.api.entity.EntityType;
import net.tridentsdk.api.nbt.*;
import net.tridentsdk.api.threads.TaskExecutor;
import net.tridentsdk.api.util.Vector;
import net.tridentsdk.api.world.World;
import net.tridentsdk.server.packets.play.out.PacketPlayOutEntityTeleport;
import net.tridentsdk.server.packets.play.out.PacketPlayOutEntityVelocity;
import net.tridentsdk.server.player.TridentPlayer;
import net.tridentsdk.server.TridentServer;
import net.tridentsdk.server.threads.EntityThreads;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Entity abstraction base
 *
 * @author The TridentSDK Team
 */
public class TridentEntity implements Entity {
    protected static final AtomicInteger counter = new AtomicInteger(-1);
    /**
     * The entity ID for the entity
     */
    protected int id;
    /**
     * The identifier UUID for the entity
     */
    protected UUID uniqueId;
    /**
     * The distance the entity has fallen
     */
    protected final AtomicLong fallDistance = new AtomicLong(0L);
    /**
     * The ticks that have passed since the entity was spawned, and alive
     */
    protected final AtomicLong ticksExisted = new AtomicLong(0L);
    /**
     * Entity task executor
     */
    protected final TaskExecutor executor = EntityThreads.entityThreadHandle(this);
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
     *
     */
    protected final AtomicInteger fireTicks = new AtomicInteger(0);
    /**
     *
     */
    protected final AtomicInteger airTicks = new AtomicInteger(0);
    /**
     *
     */
    protected boolean godMode;

    protected final AtomicInteger portalCooldown = new AtomicInteger(900);

    /**
     * Creates a new entity
     *
     * @param uniqueId      the UUID of the entity
     * @param spawnLocation the location which the entity is to be spawned
     */
    public TridentEntity(UUID uniqueId, Location spawnLocation) {
        this.uniqueId = uniqueId;
        this.id = counter.incrementAndGet();

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

    @Deprecated
    protected TridentEntity() {
        // contructor for deserializing
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

    @Override
    public boolean isNameVisible() {
        return nameVisible;
    }

    @Override
    public void applyProperties(EntityProperties properties) {
    }

    public void load(CompoundTag tag) {
        /* IDs */
        String id = ((StringTag) tag.getTag("id")).getValue(); // ID of the entity, in form of an integer
        LongTag uuidMost = tag.getTagAs("UUIDMost"); // most signifigant bits of UUID
        LongTag uuidLeast = tag.getTagAs("UUIDLeast"); // least signifigant bits of UUID

        /* Location and Velocity */
        List<NBTTag> pos = ((ListTag) tag.getTagAs("Pos")).listTags(); // 3 double tags describing x, y, z
        List<NBTTag> motion = ((ListTag) tag.getTagAs("Motion")).listTags(); // 3 double tags describing velocity
        List<NBTTag> rotation = ((ListTag) tag.getTagAs("Rotation")).listTags(); // 2 float tags describing yaw and pitch

        FloatTag fallDistance = tag.getTagAs("FallDistance"); // distance from the entity to the ground
        ShortTag fireTicks = tag.getTagAs("Fire"); // number of ticks until fire goes out
        ShortTag airTicks = tag.getTagAs("Air"); // how much air the entity has, in ticks. Tag is inverted for squids

        ByteTag onGround = tag.getTagAs("OnGround"); // 0 = false, 1 = true - True if entity is on the ground
        ByteTag invulnerable = tag.getTagAs("Invulnerable"); // 0 = false, 1 = true If god mode is enabled, essentially.

        /* Dimensions */
        IntTag dimension = tag.getTagAs("Dimension"); // no found usage; -1 for nether, 0 for overworld, 1 for end
        IntTag portalCooldown = tag.getTagAs("PortalCooldown"); // amount of ticks until entity can use a portal, starts at 900

        /* Display Name */
        StringTag displayName = tag.getTagAs("CustomName"); // Custom name for the entity, other known as display name.
        ByteTag dnVisible = tag.getTagAs("CustomNameVisible"); // 0 = false, 1 = true - If true, it will always appear above them

        ByteTag silent = tag.getTagAs("Silent"); // 0 = false, 1 = true - If true, the entity will not make a sound

        NBTTag riding = tag.getTagAs("Riding"); // CompoundTag of the entity being ridden, contents are recursive
        NBTTag commandStats = tag.getTagAs("CommandStats"); // Information to modify relative to the last command run

        /* Set data */
        this.id = Integer.parseInt(id);

        if(this.id >= counter.get()) {
            counter.incrementAndGet();
        }

        this.uniqueId = new UUID(uuidMost.getValue(), uuidLeast.getValue());

        int[] location = new int[3];

        for (int i = 0; i < 3; i += 1) {
            location[i] = ((IntTag) pos.get(i)).getValue();
        }

        // set x, y, and z cordinates from array
        loc.setX(location[0]);
        loc.setY(location[1]);
        loc.setZ(location[2]);

        int[] velocity = new int[3];

        for (int i = 0; i < 3; i += 1) {
            velocity[i] = ((IntTag) motion.get(i)).getValue();
        }

        // set velocity from array
        this.velocity.setX(velocity[0]);
        this.velocity.setY(velocity[1]);
        this.velocity.setZ(velocity[2]);

        // set yaw and pitch from NBTTag
        loc.setYaw(((IntTag) rotation.get(0)).getValue());
        loc.setPitch(((IntTag) rotation.get(0)).getValue());

        this.fallDistance.set((long) fallDistance.getValue()); // FIXME: may lose precision, consider changing AtomicLong
        this.fireTicks.set(fireTicks.getValue());
        this.airTicks.set(airTicks.getValue());
        this.portalCooldown.set(portalCooldown.getValue());

        this.onGround = onGround.getValue() == 1;
        this.godMode = invulnerable.getValue() == 1;

        this.nameVisible = dnVisible.getValue() == 1;
        this.silent = silent.getValue() == 1;
        this.displayName = displayName.getValue();
    }
}
