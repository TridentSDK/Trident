/*
 * Trident - A Multithreaded Server Alternative
 * Copyright 2016 The TridentSDK Team
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
import net.tridentsdk.server.TridentServer;
import net.tridentsdk.server.concurrent.PoolSpec;
import net.tridentsdk.server.concurrent.ServerThreadPool;
import net.tridentsdk.server.entity.meta.EntityMetaType;
import net.tridentsdk.server.entity.meta.TridentEntityMeta;
import net.tridentsdk.server.net.EntityMetadata;
import net.tridentsdk.server.packet.play.*;
import net.tridentsdk.server.player.TridentPlayer;
import net.tridentsdk.server.world.TridentWorld;
import net.tridentsdk.world.World;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The implementation class for an entity.
 */
public abstract class TridentEntity implements Entity {
    /**
     * The counter which produces the entity ID numbers
     */
    private static final AtomicInteger EID_COUNTER = new AtomicInteger();

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
     */
    public TridentEntity(World world, PoolSpec spec) {
        this.id = EID_COUNTER.incrementAndGet();
        this.position = new Position(world);
        this.pool = ServerThreadPool.forSpec(spec);

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
    public void setPosition(Position position) {
        Position delta = position.clone().subtract(this.position);

        if(delta.x() != 0 || delta.y() != 0 || delta.z() != 0) {
            if (this.position.yaw() != position.yaw() || this.position.pitch() != position.pitch()){
                PlayOutEntityLookAndRelativeMove lookAndRelativeMove = new PlayOutEntityLookAndRelativeMove(this, delta);
                PlayOutEntityHeadLook headLook = new PlayOutEntityHeadLook(this);
                TridentServer.instance().players().stream().filter(p -> !p.equals(this)).forEach(p -> {
                    ((TridentPlayer) p).net().sendPacket(lookAndRelativeMove);
                    ((TridentPlayer) p).net().sendPacket(headLook);
                });
            } else {
                PlayOutEntityRelativeMove packet = new PlayOutEntityRelativeMove(this, delta);
                TridentServer.instance().players().stream().filter(p -> !p.equals(this)).forEach(p -> ((TridentPlayer) p).net().sendPacket(packet));
            }
        }

        this.position = position;
    }

    @Override
    public TridentWorld getWorld() {
        return (TridentWorld) this.position.world();
    }

    @Override
    public final void remove() {
        this.doRemove();

        PlayOutDestroyEntities destroyEntities = new PlayOutDestroyEntities(Collections.singletonList(this));
        TridentPlayer.PLAYERS.values().stream().filter(player -> !player.equals(this)).forEach(p -> p.net().sendPacket(destroyEntities));
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
        TridentPlayer.PLAYERS.values().forEach(p -> p.net().sendPacket(packet));
    }

    /**
     * Removal hook.
     */
    public abstract void doRemove();

    /**
     * Ticking hook.
     */
    public abstract void doTick();
}
