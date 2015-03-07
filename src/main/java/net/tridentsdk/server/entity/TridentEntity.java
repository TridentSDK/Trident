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

package net.tridentsdk.server.entity;

import com.google.common.collect.Sets;
import com.google.common.util.concurrent.AtomicDouble;
import net.tridentsdk.Position;
import net.tridentsdk.base.Substance;
import net.tridentsdk.docs.InternalUseOnly;
import net.tridentsdk.docs.PossiblyThreadSafe;
import net.tridentsdk.entity.Entity;
import net.tridentsdk.entity.EntityProperties;
import net.tridentsdk.entity.EntityType;
import net.tridentsdk.factory.ExecutorFactory;
import net.tridentsdk.meta.nbt.*;
import net.tridentsdk.server.TridentServer;
import net.tridentsdk.server.data.ProtocolMetadata;
import net.tridentsdk.server.packets.play.out.PacketPlayOutDestroyEntities;
import net.tridentsdk.server.packets.play.out.PacketPlayOutEntityTeleport;
import net.tridentsdk.server.packets.play.out.PacketPlayOutEntityVelocity;
import net.tridentsdk.server.player.TridentPlayer;
import net.tridentsdk.util.Vector;
import net.tridentsdk.util.WeakEntity;
import net.tridentsdk.world.World;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Entity abstraction base
 *
 * @author The TridentSDK Team
 */
@PossiblyThreadSafe
public class TridentEntity implements Entity {
    @InternalUseOnly
    protected static final AtomicInteger counter = new AtomicInteger(-1);

    /**
     * Internal entity tracker, used to spawn the entity and track movement, etc.
     */
    protected static final EntityHandler HANDLER = EntityHandler.create();
    /**
     * The distance the entity has fallen
     */
    protected final AtomicDouble fallDistance = new AtomicDouble(0L);
    /**
     * The ticks that have passed since the entity was spawned, and alive
     */
    protected final AtomicLong ticksExisted = new AtomicLong(0L);
    /**
     * How long the entity has been on fire
     */
    protected final AtomicInteger fireTicks = new AtomicInteger(0);
    /**
     * How many ticks of air the entity has left
     */
    protected final AtomicLong airTicks = new AtomicLong();
    /**
     * Length of time the entity must wait to enter a portal. Unknown unit. TODO
     */
    protected final AtomicInteger portalCooldown = new AtomicInteger(900);
    /**
     * The entity ID for the entity
     */
    protected volatile int id;
    /**
     * The identifier UUID for the entity
     */
    protected volatile UUID uniqueId;
    /**
     * Entity task executor
     */
    protected volatile ExecutorFactory<Entity> executor;
    /**
     * The movement vector for the entity
     */
    protected volatile Vector velocity;
    /**
     * The entity location
     */
    protected volatile Position loc;
    /**
     * Whether or not the entity is touching the ground
     */
    protected volatile boolean onGround;
    /**
     * The entity's passenger, if there are any
     */
    protected volatile Entity passenger;
    /**
     * The name of the entity appearing above the head
     */
    protected volatile String displayName;
    /**
     * Whether or not the name of the entity is visible
     */
    protected volatile boolean nameVisible;
    /**
     * TODO
     */
    protected volatile boolean silent;
    /**
     * {@code true} to indicate the entity cannot be damaged
     */
    protected volatile boolean godMode;
    /**
     * Internal metadata for the entity
     */
    protected final ProtocolMetadata protocolMeta = new ProtocolMetadata();

    /**
     * Creates a new entity
     *
     * @param uniqueId      the UUID of the entity
     * @param spawnLocation the location which the entity is to be spawned
     */
    public TridentEntity(UUID uniqueId, Position spawnLocation) {
        this.uniqueId = uniqueId;
        this.id = counter.incrementAndGet();
        this.velocity = new Vector(0.0D, 0.0D, 0.0D);
        this.loc = spawnLocation;

        updateProtocolMeta();

        for (double y = this.loc.y(); y > 0.0; y--) {
            Position l = Position.create(this.loc.world(), this.loc.x(), y, this.loc.z());

            if (l.block().substance() != Substance.AIR) {
                this.fallDistance.set((long) (this.loc.y() - y));
                this.onGround = this.fallDistance.get() == 0.0D;

                break;
            }
        }
    }

    @Deprecated
    protected TridentEntity() {
        // constructor for deserializing
    }

    /**
     * Begin entity management
     *
     * @return the current entity
     */
    public TridentEntity spawn() {
        HANDLER.register(this);
        executor.assign(this);
        return this;
    }

    protected void updateProtocolMeta() {
        protocolMeta.setMeta(0, ProtocolMetadata.MetadataType.BYTE, (byte) ((fireTicks.intValue() == 0) ? 1 : 0));
        protocolMeta.setMeta(1, ProtocolMetadata.MetadataType.SHORT, airTicks.shortValue());
    }

    @Override
    public void teleport(double x, double y, double z) {
        this.teleport(Position.create(this.world(), x, y, z));
    }

    @Override
    public void teleport(Entity entity) {
        this.teleport(entity.position());
    }

    @Override
    public void teleport(Position location) {
        this.loc = location;

        for (double y = this.loc.y(); y > 0.0; y--) {
            Position l = Position.create(this.loc.world(), this.loc.x(), y, this.loc.z());

            if (l.world().blockAt(l).substance() != Substance.AIR) {
                this.fallDistance.set((long) (this.loc.y() - y));
                this.onGround = this.fallDistance.get() == 0.0D;

                break;
            }
        }

        TridentPlayer.sendAll(new PacketPlayOutEntityTeleport().set("entityId", this.id)
                .set("location", this.loc)
                .set("onGround", this.onGround));
    }

    @Override
    public World world() {
        return this.loc.world();
    }

    @Override
    public Position position() {
        return this.loc;
    }

    public void setLocation(Position loc) {
        this.loc = loc;
    }

    @Override
    public Vector velocity() {
        return this.velocity;
    }

    @Override
    public void setVelocity(Vector vector) {
        this.velocity = vector;

        TridentPlayer.sendAll(new PacketPlayOutEntityVelocity().set("entityId", this.id).set("velocity", vector));
    }

    @Override
    public String displayName() {
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
    public UUID uniqueId() {
        return this.uniqueId;
    }

    public void tick() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                ticksExisted.incrementAndGet();
                doTick();
            }
        });
    }

    protected void doTick() {
    }

    @Override
    public boolean onGround() {
        return this.onGround;
    }

    @Override
    public Set<Entity> withinRange(double radius) {
        double squared = radius * radius;
        Set<Entity> entities = position().world().entities();
        Set<Entity> near = Sets.newHashSet();
        for (Entity entity : entities) {
            if (entity.position().distanceSquared(position()) <= squared)
                near.add(entity);
        }

        return near;
    }

    @Override
    public int entityId() {
        return this.id;
    }

    @Override
    public void remove() {
        PacketPlayOutDestroyEntities packet = new PacketPlayOutDestroyEntities();
        packet.set("destroyedEntities", new int[] { entityId() });
        TridentPlayer.sendAll(packet);
        HANDLER.removeEntity(this);
        try {
            WeakEntity.clearReferencesTo(this);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Entity passenger() {
        return this.passenger;
    }

    @Override
    public void setPassenger(Entity entity) {
        this.passenger = entity;

        // TODO: Update clients
    }

    @Override
    public void eject() {
        // TODO
    }

    @Override
    public EntityType type() {
        return null;
    }

    @Override
    public boolean isNameVisible() {
        return nameVisible;
    }

    @Override
    public void applyProperties(EntityProperties properties) {
    }

    /**
     * Moves the entity to the new coordinates. Not for teleportation.
     *
     * @param newCoords the new location for the entity
     */
    public void doMove(Position newCoords) {
        HANDLER.trackMovement(this, position(), newCoords);
        this.setLocation(newCoords);
    }

    public void load(CompoundTag tag) {
        /* IDs */
        if(!(tag.getTag("id") instanceof NullTag)) {
            // players will not have this value
            String type = ((StringTag) tag.getTag("id")).value(); // EntityType, in form of a string
        }
        LongTag uuidMost = tag.getTagAs("UUIDMost"); // most signifigant bits of UUID
        LongTag uuidLeast = tag.getTagAs("UUIDLeast"); // least signifigant bits of UUID

        /* Location and Velocity */
        List<NBTTag> pos = ((ListTag) tag.getTagAs("Pos")).listTags(); // 3 double tags describing x, y, z
        List<NBTTag> motion = ((ListTag) tag.getTagAs("Motion")).listTags(); // 3 double tags describing velocity
        List<NBTTag> rotation = ((ListTag) tag.getTagAs(
                "Rotation")).listTags(); // 2 float tags describing yaw and pitch

        FloatTag fallDistance = tag.getTagAs("FallDistance"); // distance from the entity to the ground
        ShortTag fireTicks = tag.getTagAs("Fire"); // number of ticks until fire goes out
        ShortTag airTicks = tag.getTagAs("Air"); // how much air the entity has, in ticks. Tag is inverted for squids

        ByteTag onGround = tag.getTagAs("OnGround"); // 0 = false, 1 = true - True if entity is on the ground
        ByteTag invulnerable = tag.getTagAs("Invulnerable"); // 0 = false, 1 = true If god mode is enabled, essentially.

        /* Dimensions */
        IntTag dimension = tag.getTagAs("Dimension"); // no found usage; -1 for nether, 0 for overworld, 1 for end
        IntTag portalCooldown = tag.getTagAs(
                "PortalCooldown"); // amount of ticks until entity can use a portal, starts at 900

        /* Display Name */
        StringTag displayName = (tag.containsTag("CustomName")) ? (StringTag) tag.getTag("CustomName") : new StringTag(
                "CustomName").setValue(""); // Custom name for the entity, other known as display name.
        ByteTag dnVisible = (tag.containsTag("CustomNameVisible")) ? (ByteTag) tag.getTag(
                "CustomNameVisible") : new ByteTag("CustomNameVisible").setValue(
                (byte) 0); // 0 = false, 1 = true - If true, it will always appear above them

        ByteTag silent = (tag.containsTag("Silent")) ? (ByteTag) tag.getTag("Silent") : new ByteTag("Silent").setValue(
                (byte) 0); // 0 = false, 1 = true - If true, the entity will not make a sound

        NBTTag riding = tag.getTagAs("Riding"); // CompoundTag of the entity being ridden, contents are recursive
        NBTTag commandStats = tag.getTagAs("CommandStats"); // Information to modify relative to the last command run

        /* Set data */
        this.id = counter.incrementAndGet();

        // TODO this is temporary for testing
        loc = Position.create(TridentServer.WORLD, 0, 0, 0);
        velocity = new Vector(0, 0, 0);

        this.uniqueId = new UUID(uuidMost.value(), uuidLeast.value());

        double[] location = new double[3];

        for (int i = 0; i < 3; i += 1) {
            NBTTag t = pos.get(i);

            if (t instanceof DoubleTag) {
                location[i] = ((DoubleTag) t).value();
            } else {
                location[i] = ((IntTag) t).value();
            }
        }

        // set x, y, and z cordinates from array
        loc.setX(location[0]);
        loc.setY(location[1]);
        loc.setZ(location[2]);

        double[] velocity = new double[3];

        for (int i = 0; i < 3; i += 1) {
            NBTTag t = motion.get(i);

            if (t instanceof DoubleTag) {
                velocity[i] = ((DoubleTag) t).value();
            } else {
                velocity[i] = ((IntTag) t).value();
            }
        }

        // set velocity from array
        this.velocity.setX(velocity[0]);
        this.velocity.setY(velocity[1]);
        this.velocity.setZ(velocity[2]);

        // set yaw and pitch from NBTTag
        if (rotation.get(0) instanceof IntTag) {
            loc.setYaw(((IntTag) rotation.get(0)).value());
        } else {
            loc.setYaw(((FloatTag) rotation.get(0)).value());
        }

        if (rotation.get(1) instanceof IntTag) {
            loc.setPitch(((IntTag) rotation.get(1)).value());
        } else {
            loc.setPitch(((FloatTag) rotation.get(1)).value());
        }

        this.fallDistance.set(
                (long) fallDistance.value()); // FIXME: may lose precision, consider changing AtomicLong
        this.fireTicks.set(fireTicks.value());
        this.airTicks.set(airTicks.value());
        this.portalCooldown.set(portalCooldown.value());

        this.onGround = onGround.value() == 1;
        this.godMode = invulnerable.value() == 1;

        this.nameVisible = dnVisible.value() == 1;
        this.silent = silent.value() == 1;
        this.displayName = displayName.value();
    }
}
