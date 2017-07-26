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
package net.tridentsdk.server.entity;

import lombok.Getter;
import lombok.Setter;
import net.tridentsdk.base.Position;
import net.tridentsdk.entity.Entity;
import net.tridentsdk.entity.living.Player;
import net.tridentsdk.server.concurrent.PoolSpec;
import net.tridentsdk.server.concurrent.ServerThreadPool;
import net.tridentsdk.server.entity.meta.EntityMetaType;
import net.tridentsdk.server.entity.meta.TridentEntityMeta;
import net.tridentsdk.server.net.EntityMetadata;
import net.tridentsdk.server.packet.PacketOut;
import net.tridentsdk.server.packet.play.*;
import net.tridentsdk.server.player.RecipientSelector;
import net.tridentsdk.server.player.TridentPlayer;
import net.tridentsdk.server.world.TridentChunk;
import net.tridentsdk.server.world.TridentWorld;

import javax.annotation.concurrent.ThreadSafe;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/**
 * The implementation class for an entity.
 */
@ThreadSafe
public abstract class TridentEntity implements Entity {
    /**
     * The counter which produces the entity ID numbers
     */
    public static final AtomicInteger EID_COUNTER = new AtomicInteger();

    // THREADING MECHANICS
    /**
     * Thread pool used to scheduling entity-related tasks
     * such as ticking.
     */
    protected final ServerThreadPool pool;
    /**
     * Task initialized to execute {@link #doTick()} in
     * order to prevent initializing of a runnable per
     * tick.
     */
    private final Runnable tickingTask = this::doTick;

    /**
     * The ID number assigned to this entity
     */
    @Getter
    private final int id;
    /**
     * The position at which this entity is located
     */
    @Getter
    protected volatile Position position;
    /**
     * Whether or not this entity is on the ground
     */
    @Getter
    @Setter
    private volatile boolean onGround;
    /**
     * Entity Metadata
     */
    @Getter
    private final TridentEntityMeta metadata;

    /**
     * Entity superconstructor.
     *
     * @param world the world which the entity is located
     * @param spec the specifications of the pool
     */
    public TridentEntity(TridentWorld world, PoolSpec spec) {
        this.id = EID_COUNTER.incrementAndGet();
        this.pool = ServerThreadPool.forSpec(spec);

        Position pos = world.getWorldOptions().getSpawn().toPosition(world);
        this.position = pos;

        if (this instanceof Player) {
            TridentPlayer player = (TridentPlayer) this;
            world.getOccupants().add(player);
            world.getChunkAt(pos.getChunkX(), pos.getChunkZ()).getOccupants().add(player);
        } else {
            world.getEntitySet().add(this);
            world.getEntitySet().add(this);
        }

        EntityMetaType metaType = this.getClass().getAnnotation(EntityMetaType.class);
        if (metaType == null) {
            throw new RuntimeException(this.getClass() + " doesn't have an EntityMetaType annotation!");
        }

        try {
            this.metadata = metaType.value().getConstructor(EntityMetadata.class).newInstance(new EntityMetadata());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public final void setPosition(Position position) {
        Position old = this.position;
        this.position = position;

        TridentWorld fromWorld = (TridentWorld) old.getWorld();
        TridentWorld destWorld = (TridentWorld) position.getWorld();
        if (!destWorld.equals(fromWorld)) {
            if (this instanceof Player) {
                fromWorld.getOccupants().remove(this);
                destWorld.getOccupants().add((TridentPlayer) this);
            } else {
                fromWorld.getEntitySet().remove(this);
                destWorld.getEntitySet().add(this);
            }
        }

        int destCX = position.getChunkX();
        int destCZ = position.getChunkZ();
        TridentChunk destChunk = destWorld.getChunkAt(destCX, destCZ);
        int fromCX = old.getChunkX();
        int fromCZ = old.getChunkZ();
        if (fromCX != destCX || fromCZ != destCZ) {
            TridentChunk fromChunk = fromWorld.getChunkAt(fromCX, fromCZ, false);
            List<Entity> destroy = Collections.singletonList(this);

            PacketOut spawnThis = this.getSpawnPacket();
            if (this instanceof Player) {
                TridentPlayer player = (TridentPlayer) this;
                if (fromChunk != null) {
                    fromChunk.getOccupants().remove(player);
                }
                destChunk.getOccupants().add(player);

                Stream.concat(fromChunk == null ? Stream.empty() : fromChunk.getHolders().stream(), destChunk.getHolders().stream()).
                        distinct().
                        forEach(p -> {
                            if (fromChunk == null || !fromChunk.getHolders().contains(p)) {
                                if (p.equals(this)) {
                                    return;
                                }

                                p.net().sendPacket(spawnThis);
                            }

                            if (!destChunk.getHolders().contains(p)) {
                                p.net().sendPacket(new PlayOutDestroyEntities(destroy));
                            }
                        });
                player.updateChunks();
            } else {
                if (fromChunk != null) {
                    fromChunk.getEntitySet().remove(this);
                }

                destChunk.getEntitySet().add(this);

                Stream.concat(fromChunk == null ? Stream.empty() : fromChunk.getHolders().stream(), destChunk.getHolders().stream()).
                        distinct().
                        forEach(p -> {
                            if (fromChunk == null || !fromChunk.getHolders().contains(p)) {
                                if (p.equals(this)) {
                                    return;
                                }

                                p.net().sendPacket(spawnThis);
                            }

                            if (!destChunk.getHolders().contains(p)) {
                                p.net().sendPacket(new PlayOutDestroyEntities(destroy));
                            }
                        });
            }
        }

        Position delta = position.subtract(old);
        if(delta.getX() != 0 || delta.getY() != 0 || delta.getZ() != 0) {
            if (old.distanceSquared(position) > 16) {
                PlayOutTeleport packet = new PlayOutTeleport(this, position);
                RecipientSelector.whoCanSee(destChunk, null, packet);
            } else {
                if (Double.compare(old.getYaw(), position.getYaw()) == 0 && Double.compare(old.getPitch(), position.getPitch()) == 0){
                    PlayOutEntityRelativeMove packet = new PlayOutEntityRelativeMove(this, delta);
                    RecipientSelector.whoCanSee(destChunk, this, packet);
                } else {
                    PlayOutEntityLookAndRelativeMove lookAndRelativeMove = new PlayOutEntityLookAndRelativeMove(this, delta);
                    PlayOutEntityHeadLook look = new PlayOutEntityHeadLook(this);
                    RecipientSelector.whoCanSee(destChunk, this, lookAndRelativeMove, look);
                }
            }
        } else if (Float.compare(old.getYaw(), position.getYaw()) != 0 || Float.compare(old.getPitch(), position.getPitch()) != 0) {
            PlayOutEntityLookAndRelativeMove lookAndRelativeMove = new PlayOutEntityLookAndRelativeMove(this, delta);
            PlayOutEntityHeadLook look = new PlayOutEntityHeadLook(this);
            RecipientSelector.whoCanSee(destChunk, this, lookAndRelativeMove, look);
        }
    }

    @Override
    public TridentWorld getWorld() {
        return (TridentWorld) this.position.getWorld();
    }

    @Override
    public final void remove() {
        TridentWorld world = (TridentWorld) this.position.getWorld();
        world.getEntitySet().remove(this);
        world.getOccupants().remove(this);

        TridentChunk chunk = world.getChunkAt(this.position.getChunkX(), this.position.getChunkZ(), false);
        if (chunk != null) {
            if (this instanceof Player) {
                chunk.getOccupants().remove(this);
            } else {
                chunk.getEntitySet().remove(this);
            }
        }

        if (this instanceof Player) {
            world.getOccupants().remove(this);
        } else {
            world.getEntitySet().remove(this);
        }

        this.doRemove();

        PlayOutDestroyEntities destroyEntities = new PlayOutDestroyEntities(Collections.singletonList(this));
        TridentPlayer.getPlayers().values().stream().filter(player -> !player.equals(this)).forEach(p -> p.net().sendPacket(destroyEntities));
    }

    /**
     * Ticks the entity.
     */
    public final void tick() {
        // Performs #doTick()
        this.pool.execute(this.tickingTask);
    }

    @Override
    public void updateMetadata() {
        PlayOutEntityMetadata packet = new PlayOutEntityMetadata(this);
        RecipientSelector.whoCanSee(this, false, packet);
    }

    /**
     * Removal hook.
     */
    public abstract void doRemove();

    /**
     * Ticking hook.
     */
    public abstract void doTick();

    /**
     * Obtains the entity spawn packet.
     *
     * @return the packet which is sent to spawn the entity
     * for a client
     */
    public abstract PacketOut getSpawnPacket();
}
