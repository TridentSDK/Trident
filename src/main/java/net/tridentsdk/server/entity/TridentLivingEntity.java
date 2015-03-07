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

import com.google.common.util.concurrent.AtomicDouble;
import net.tridentsdk.Position;
import net.tridentsdk.Trident;
import net.tridentsdk.entity.*;
import net.tridentsdk.entity.living.Player;
import net.tridentsdk.entity.living.ai.AiModule;
import net.tridentsdk.entity.living.ai.Path;
import net.tridentsdk.meta.nbt.*;
import net.tridentsdk.server.packets.play.out.PacketPlayOutDestroyEntities;
import net.tridentsdk.server.packets.play.out.PacketPlayOutSpawnMob;
import net.tridentsdk.server.player.TridentPlayer;
import net.tridentsdk.util.Vector;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import static net.tridentsdk.server.data.ProtocolMetadata.MetadataType;

/**
 * An entity that has health
 *
 * @author The TridentSDK Team
 */
public abstract class TridentLivingEntity extends TridentEntity implements LivingEntity {
    private volatile AiModule ai;
    private volatile Path path;

    protected final List<EntityAttribute> attributes = new CopyOnWriteArrayList<>();
    protected final AtomicInteger invincibilityTicks = new AtomicInteger(0);
    protected final AtomicInteger restTicks = new AtomicInteger(0);
    /**
     * The entity health
     */
    protected final AtomicDouble health = new AtomicDouble(0.0);
    /**
     * Whether the entity is dead
     */
    protected volatile boolean dead;
    /**
     * Whether the entity can pick up items
     */
    protected volatile boolean canPickup = true;
    /**
     * The maximum available health
     */
    protected volatile double maxHealth;

    /**
     * Inherits from {@link TridentEntity}
     *
     * <p>The entity is immediately set "non-dead" after {@code super} call</p>
     */
    public TridentLivingEntity(UUID id, Position spawnLocation) {
        super(id, spawnLocation);

        this.dead = false;
    }

    @Override
    protected void updateProtocolMeta() {
        super.updateProtocolMeta();

        protocolMeta.setMeta(2, MetadataType.STRING, displayName);
        protocolMeta.setMeta(3, MetadataType.BYTE, nameVisible ? (byte) 1 : (byte) 0);
        protocolMeta.setMeta(6, MetadataType.FLOAT, health.floatValue());
        protocolMeta.setMeta(7, MetadataType.INT, 0);
        protocolMeta.setMeta(8, MetadataType.BYTE, (byte) 1); // TODO (potion effects)
        protocolMeta.setMeta(9, MetadataType.BYTE, (byte) 0); // TODO (arrows in entity)
        protocolMeta.setMeta(15, MetadataType.BYTE, (ai == null) ? (byte) 1 : (byte) 0);
    }

    @Override
    protected void doTick() {
        performAiUpdate();
    }

    @Override
    public double health() {
        return this.health.get();
    }

    @Override
    public void setHealth(double health) {
        this.health.set(health);
    }

    @Override
    public double maxHealth() {
        return this.maxHealth;
    }

    @Override
    public void setMaxHealth(double maxHealth) {
        this.maxHealth = maxHealth;
    }

    @Override
    public Position headLocation() {
        return this.position().relative(new Vector(0.0d, 1.0d, 0.0d));
    }

    @Override
    public long remainingAir() {
        return this.airTicks.get();
    }

    @Override
    public void setRemainingAir(long ticks) {
        this.airTicks.set((int) ticks);
    }

    @Override
    public boolean canCollectItems() {
        return this.canPickup;
    }

    @Override
    public boolean isDead() {
        return this.dead;
    }

    @Override
    public void remove() {
        dead = true;
        super.remove();
    }

    @Override
    public void setAiModule(AiModule module) {
        this.ai = module;
    }

    @Override
    public AiModule aiModule() {
        AiModule module = this.ai;
        if (module == null) {
            return Trident.instance().aiHandler().defaultAiFor(type());
        } else {
            return module;
        }
    }

    @Override
    public <T extends Projectile> T launchProjectile(EntityProperties properties) {
        return null;
    }

    public void performAiUpdate() {
        AiModule module = this.aiModule();

        if (this.restTicks.get() <= 0) {
            this.restTicks.set(module.think(this));
        } else {
            this.restTicks.getAndDecrement();
            // TODO: follow path
        }
    }

    @Override
    public Path path() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    @Override
    public void hide(Entity entity) {
        PacketPlayOutDestroyEntities packet = new PacketPlayOutDestroyEntities();
        packet.set("destroyedEntities", new int[]{ entity.entityId() });

        if (this instanceof Player) {
            ((TridentPlayer) this).connection().sendPacket(packet);
        }
    }

    @Override
    public void show(Entity entity) {
        PacketPlayOutSpawnMob packet = new PacketPlayOutSpawnMob();
        packet.set("entityId", entity.entityId())
                .set("entity", entity)
                .set("metadata", ((TridentEntity) entity).protocolMeta);

        if (this instanceof Player) {
            ((TridentPlayer) this).connection().sendPacket(packet);
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);

        if (type() == EntityType.PLAYER) {
            return; // players do not inherit the living entity or "mob" NBT structure
        }

        if (tag.containsTag("HealF")) {
            health.set(((FloatTag) tag.getTag("HealF")).value());
        } else {
            health.set(((ShortTag) tag.getTag("Health")).value());
        }

        FloatTag extraHealth = tag.getTagAs("AbsorptionAmount"); // health added if the entity has the absorption effect

        ShortTag invincibilityTicks = tag.getTagAs("AttackTime"); // time in ticks that the entity is invincible
        ShortTag hurtTime = tag.getTagAs("HurtTime"); // time in ticks that the entity is shown as "red" for being hit
        ShortTag timeDead = tag.getTagAs("DeathTime"); // time in ticks entity has been dead for

        ListTag attributes = tag.getTagAs("Attributes");
        ListTag potionEffects = tag.getTagAs("ActiveEffects");

        ByteTag canPickupLoot = tag.getTagAs("CanPickupLoot");
        ByteTag aiDisabled = tag.getTagAs("NoAI");
        ByteTag canRespawn = tag.getTagAs("PersistenceRequired");
        ByteTag leashed = tag.getTagAs("Leashed");

        health.addAndGet(extraHealth.value());
        this.invincibilityTicks.set(invincibilityTicks.value());

        for (NBTTag attribute : attributes.listTags()) {
            this.attributes.add(NBTSerializer.deserialize(EntityAttribute.class,
                    attribute.asType(CompoundTag.class)));
        }
    }
}
