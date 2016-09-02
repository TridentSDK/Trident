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

import net.tridentsdk.base.Position;
import net.tridentsdk.entity.Entity;
import net.tridentsdk.server.entity.meta.EntityMetaType;
import net.tridentsdk.server.entity.meta.TridentEntityMeta;
import net.tridentsdk.server.net.EntityMetadata;
import net.tridentsdk.server.packet.play.PlayOutDestroyEntities;
import net.tridentsdk.server.packet.play.PlayOutEntityMetadata;
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

    /**
     * The ID number assigned to this entity
     */
    private final int id;
    /**
     * The position at which this entity is located
     */
    private volatile Position position;
    /**
     * Whether or not this entity is on the ground
     */
    private volatile boolean onGround;
    /**
     * Entity Metadata
     */
    private final TridentEntityMeta metadata;

    /**
     * Entity superconstructor.
     *
     * @param world the world which the entity is located
     */
    public TridentEntity(World world) {
        this.id = EID_COUNTER.incrementAndGet();
        this.position = new Position(world);

        EntityMetaType metaType = this.getClass().getAnnotation(EntityMetaType.class);
        if(metaType == null){
            throw new RuntimeException(this.getClass() + " doesn't have an EntityMetaType annotation!");
        }

        TridentEntityMeta metadata = null;

        try {
            metadata = metaType.value().getConstructor(EntityMetadata.class).newInstance(new EntityMetadata());
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.metadata = metadata;
    }

    @Override
    public int id() {
        return this.id;
    }

    @Override
    public Position position() {
        return this.position;
    }

    @Override
    public void setPosition(Position position) {
        this.position = position;
        // TODO move packet
    }

    @Override
    public boolean onGround() {
        return this.onGround;
    }

    /**
     * Sets the on ground status of the entity.
     *
     * @param onGround {@code true} if on ground
     */
    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    @Override
    public TridentWorld world() {
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
        this.doTick();
    }

    /**
     * Removal hook.
     */
    public abstract void doRemove();

    /**
     * Ticking hook.
     */
    public abstract void doTick();

    public TridentEntityMeta getMetadata() {
        return metadata;
    }

    @Override
    public void updateMetadata() {
        PlayOutEntityMetadata packet = new PlayOutEntityMetadata(this);
        TridentPlayer.PLAYERS.values().forEach(p -> p.net().sendPacket(packet));
    }

}
