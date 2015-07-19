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

package net.tridentsdk.server.player;


import net.tridentsdk.GameMode;
import net.tridentsdk.Position;
import net.tridentsdk.Trident;
import net.tridentsdk.entity.Entity;
import net.tridentsdk.entity.Projectile;
import net.tridentsdk.entity.living.Player;
import net.tridentsdk.entity.traits.EntityProperties;
import net.tridentsdk.entity.traits.PlayerSpeed;
import net.tridentsdk.event.entity.EntityDamageEvent;
import net.tridentsdk.meta.nbt.*;
import net.tridentsdk.registry.Factory;
import net.tridentsdk.server.TridentServer;
import net.tridentsdk.server.data.Slot;
import net.tridentsdk.server.entity.TridentInventoryHolder;
import net.tridentsdk.server.window.TridentInventory;
import net.tridentsdk.server.world.TridentWorld;
import net.tridentsdk.util.TridentLogger;
import net.tridentsdk.window.Inventory;
import net.tridentsdk.world.Dimension;
import net.tridentsdk.world.World;

import javax.annotation.concurrent.ThreadSafe;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ThreadSafe
public class OfflinePlayer extends TridentInventoryHolder implements Player {
    static final Map<UUID, OfflinePlayer> OFFLINE_PLAYERS = new ConcurrentHashMap<>();

    /**
     * The name of the player
     */
    protected volatile String name;
    /**
     * The dimension of the player
     */
    protected volatile Dimension dimension;
    /**
     * The gamemode the player is currently in
     */
    protected volatile GameMode gameMode;
    /**
     * TODO
     */
    protected volatile int score;
    /**
     * The current slot selected by the player
     */
    protected volatile short selectedSlot;
    /**
     * The spawn location of the player
     */
    protected volatile Position spawnLocation;
    /**
     * The current hunger of the player
     */
    protected volatile short hunger;
    /**
     * The exaustion of the player
     */
    protected volatile float exhaustion;
    /**
     * The current food saturation of the player
     */
    protected volatile float saturation;
    /**
     * The next ticks that will be run before the player drops in hunger
     */
    protected volatile int foodTickTimer;
    /**
     * The player's experience level
     */
    protected volatile int xpLevel;
    /**
     * The percentage of experience currently in the level
     */
    protected volatile float xpPercent;
    /**
     * The total numerical experience the player has
     */
    protected volatile int xpTotal;
    /**
     * The experience seed of the player
     */
    protected volatile int xpSeed;

    protected final Inventory enderChest = null;
    protected final PlayerAbilities abilities = new PlayerAbilities();
    protected final PlayerSpeed playerSpeed = new PlayerSpeedImpl();
    protected final Set<String> permissions = Factory.newSet();

    OfflinePlayer(UUID uuid, CompoundTag tag, TridentWorld world) {
        super(uuid, world.spawnPosition());

        load(tag);

        dimension = Dimension.of(((IntTag) tag.getTag("Dimension")).value());
        gameMode = GameMode.of(((IntTag) tag.getTag("playerGameType")).value());
        score = ((IntTag) tag.getTag("Score")).value();
        selectedSlot = (short) ((IntTag) tag.getTag("SelectedItemSlot")).value();

        if (tag.containsTag("SpawnX")) {
            spawnLocation = Position.create(world, ((IntTag) tag.getTag("SpawnX")).value(),
                    ((IntTag) tag.getTag("SpawnY")).value(), ((IntTag) tag.getTag("SpawnZ")).value());
        } else {
            spawnLocation = world.spawnPosition();
        }

        hunger = (short) ((IntTag) tag.getTag("foodLevel")).value();
        exhaustion = ((FloatTag) tag.getTag("foodExhaustionLevel")).value();
        saturation = ((FloatTag) tag.getTag("foodSaturationLevel")).value();
        foodTickTimer = ((IntTag) tag.getTag("foodTickTimer")).value();
        xpLevel = ((IntTag) tag.getTag("XpLevel")).value();
        xpPercent = ((FloatTag) tag.getTag("XpP")).value();
        xpTotal = ((IntTag) tag.getTag("XpLevel")).value();
        xpSeed = tag.containsTag("XpSeed") ? ((IntTag) tag.getTag("XpSeed")).value() :
                new IntTag("XpSeed").setValue(0).value();

        // TODO come up with a valid implementation of this...?
        inventory = new TridentInventory(44);
        for (NBTTag t : ((ListTag) tag.getTag("Inventory")).listTags()) {
            Slot slot = NBTSerializer.deserialize(Slot.class, (CompoundTag) t);

            //window.setSlot(slot.getSlot(), slot.toItemStack());
        }

        for (NBTTag t : ((ListTag) tag.getTag("EnderItems")).listTags()) {
            Slot slot = NBTSerializer.deserialize(Slot.class, (CompoundTag) t);

            //enderChest.setSlot(slot.getSlot(), slot.toItemStack());
        }

        NBTSerializer.deserialize(abilities, (CompoundTag) tag.getTag("abilities"));
    }

    public static OfflinePlayer getOfflinePlayer(UUID id) {
        return OFFLINE_PLAYERS.get(id);
    }

    public static CompoundTag generatePlayer(UUID id) {
        // DEBUG =====
        World defaultWorld = TridentServer.WORLD;
        // =====
        Position spawnLocation = defaultWorld.spawnPosition();
        CompoundTagBuilder<NBTBuilder> builder = NBTBuilder.newBase(id.toString());

        builder.stringTag("id", String.valueOf(counter.incrementAndGet()));
        builder.longTag("UUIDMost", id.getMostSignificantBits());
        builder.longTag("UUIDLeast", id.getLeastSignificantBits());

        ListTagBuilder<CompoundTagBuilder<NBTBuilder>> pos = builder.beginListTag("Pos", TagType.DOUBLE);

        pos.tag(spawnLocation.x());
        pos.tag(spawnLocation.y());
        pos.tag(spawnLocation.z());

        builder = pos.endListTag();

        ListTagBuilder<CompoundTagBuilder<NBTBuilder>> motion = builder.beginListTag("Motion", TagType.DOUBLE);

        motion.tag(0d);
        motion.tag(0d);
        motion.tag(0d);

        builder = motion.endListTag();

        ListTagBuilder<CompoundTagBuilder<NBTBuilder>> rotation = builder.beginListTag("Rotation", TagType.FLOAT);

        rotation.tag(0f);
        rotation.tag(0f);

        builder = rotation.endListTag();

        builder.floatTag("FallDistance", 0);
        builder.shortTag("Fire", (short) -20);
        builder.shortTag("Air", (short) 0);

        builder.byteTag("OnGround", (byte) 1);
        builder.byteTag("Invulnerable", (byte) 0);

        builder.intTag("Dimension", Dimension.OVERWORLD.asByte());
        builder.intTag("PortalCooldown", 900);

        builder.stringTag("CustomName", "");
        // does not apply to onlinePlayers
        //builder.byteTag("CustomNameVisible", (byte) 0);

        builder.byteTag("Silent", (byte) 0);

        builder.compoundTag(new CompoundTag("Riding"));

        builder.intTag("Dimension", Dimension.OVERWORLD.asByte());
        builder.intTag("playerGameType", Trident.config().getByte("default-gamemode"));
        builder.intTag("Score", 0);
        builder.intTag("SelectedGameSlot", 0);

        builder.intTag("foodLevel", 20);
        builder.floatTag("foodExhaustionLevel", 0F);
        builder.floatTag("foodSaturationLevel", 0F);
        builder.intTag("foodTickTimer", 0);

        builder.intTag("XpLevel", 0);
        builder.floatTag("XpP", 0);
        builder.intTag("XpLevel", 0);
        builder.intTag("XpSeed", 0); // actually give a proper seed

        builder.listTag(new ListTag("Inventory", TagType.COMPOUND));
        builder.listTag(new ListTag("EnderItems", TagType.COMPOUND));

        builder.intTag("SelectedItemSlot", 0);

        builder.compoundTag(NBTSerializer.serialize(new PlayerAbilities(), "abilities"));

        return builder.endCompoundTag().build();
    }

    public Position spawnLocation() {
        return spawnLocation;
    }

    @Override
    public Locale locale() {
        return null;
    }

    @Override
    public GameMode gameMode() {
        return gameMode;
    }

    @Override
    public void setGameMode(GameMode mode) {
        this.gameMode = mode;
        this.abilities.creative = (byte) ((mode == GameMode.CREATIVE) ? 1 : 0);
        this.abilities.canFly =  (byte) ((mode == GameMode.CREATIVE) ? 1 : 0);
    }

    @Override
    public PlayerSpeed speedModifiers() {
        return playerSpeed;
    }

    @Override
    public void sendMessage(String message) {
        TridentLogger.error(new UnsupportedOperationException("You can't send messages to a non-existant player"));
    }

    @Override
    public boolean connected() {
        return false;
    }

    @Override
    public void invokeCommand(String message) {
        TridentLogger.error(new UnsupportedOperationException("You cannot make an OfflinePlayer invoke a command!"));
    }

    @Override
    public String lastCommand() {
        return null;
    }

    @Override
    public boolean isOperator() {
        // DEBUG ===== Everyone is OP'd!
        return true;
        // =====
    }

    @Override
    public void hide(Entity entity) {
        TridentLogger.error(new UnsupportedOperationException("You cannot hide an entity from an OfflinePlayer!"));
    }

    @Override
    public void show(Entity entity) {
        TridentLogger.error(new UnsupportedOperationException("You cannot show an entity to an OfflinePlayer!"));
    }

    @Override
    public EntityDamageEvent lastDamageEvent() {
        return null;
    }

    @Override
    public Player lastPlayerDamager() {
        return null;
    }

    @Override
    public void sendRaw(String... messages) {
        TridentLogger.error(new UnsupportedOperationException("You cannot send a message to an OfflinePlayer!"));
    }

    @Override
    public String lastMessage() {
        return null;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public <T extends Projectile> T launchProjectile(EntityProperties properties) {
        TridentLogger.error(new UnsupportedOperationException("You cannot make an OfflinePlayer launch a projectile!"));
        return null;
    }

    public CompoundTag asNbt() {
        CompoundTag tag = new CompoundTag(uniqueId().toString());

        tag.addTag(new LongTag("UUIDMost").setValue(uniqueId.getMostSignificantBits()));
        tag.addTag(new LongTag("UUIDLeast").setValue(uniqueId.getLeastSignificantBits()));

        tag.addTag(new IntTag("Dimension").setValue(dimension.asByte()));
        tag.addTag(new IntTag("playerGameType").setValue(gameMode.asByte()));
        tag.addTag(new IntTag("Score").setValue(score));
        tag.addTag(new IntTag("SelectedItemSlot").setValue(selectedSlot));

        //tag.addTag(NBTSerializer.serialize(new Slot(itemInHand())));
        tag.addTag(new IntTag("SpawnX").setValue((int) spawnLocation.x()));
        tag.addTag(new IntTag("SpawnY").setValue((int) spawnLocation.y()));
        tag.addTag(new IntTag("SpawnZ").setValue((int) spawnLocation.z()));

        tag.addTag(new IntTag("foodLevel").setValue(hunger));
        tag.addTag(new FloatTag("foodExhaustionLevel").setValue(exhaustion));
        tag.addTag(new FloatTag("foodSaturationLevel").setValue(saturation));
        tag.addTag(new IntTag("foodTickTimer").setValue(foodTickTimer));

        tag.addTag(new IntTag("XpLevel").setValue(xpLevel));
        tag.addTag(new FloatTag("XpP").setValue(xpPercent));
        tag.addTag(new IntTag("XpTotal").setValue(xpTotal));
        tag.addTag(new IntTag("XpSeed").setValue(xpSeed));

        tag.addTag(new ByteTag("Invulnerable").setValue(godMode));
        tag.addTag(new IntTag("PortalCooldown").setValue(portalCooldown.get()));
        tag.addTag(new FloatTag("FallDistance").setValue(fallDistance.floatValue()));
        tag.addTag(new ByteTag("OnGround").setValue(onGround));
        tag.addTag(new ShortTag("Fire").setValue(fireTicks.shortValue()));
        tag.addTag(new ShortTag("Air").setValue((short) airTicks.get()));
        tag.addTag(new ByteTag("Silent").setValue(silent));
        tag.addTag(new IntTag("SelectedItemSlot").setValue(selectedSlot));

        ListTag position = new ListTag("Pos",TagType.DOUBLE);
        position.addTag(new DoubleTag("").setValue(loc.x()));
        position.addTag(new DoubleTag("").setValue(loc.y()));
        position.addTag(new DoubleTag("").setValue(loc.z()));

        tag.addTag(position);

        ListTag motion = new ListTag("Motion",TagType.DOUBLE) ;
        motion.addTag(new DoubleTag("").setValue(velocity.x()));
        motion.addTag(new DoubleTag("").setValue(velocity.y()));
        motion.addTag(new DoubleTag("").setValue(velocity.z()));

        tag.addTag(motion);

        ListTag rotation = new ListTag("Rotation", TagType.FLOAT);
        rotation.addTag(new FloatTag("").setValue(loc.yaw()));
        rotation.addTag(new FloatTag("").setValue(loc.pitch()));

        tag.addTag(rotation);

        ListTag inventoryTag = new ListTag("Inventory", TagType.COMPOUND);

        /*for (ItemStack is : window.items()) {
            inventoryTag.addTag(NBTSerializer.serialize(new Slot(is)));
        }*/

        tag.addTag(inventoryTag);

        ListTag enderTag = new ListTag("EnderItems", TagType.COMPOUND);

        /*for (ItemStack is : enderChest.items()) {
            enderTag.addTag(NBTSerializer.serialize(new Slot(is)));
        }*/

        tag.addTag(enderTag);
        tag.addTag(NBTSerializer.serialize(abilities, "abilities"));

        return tag;
    }

    @Override
    public void grantPermission(String perm) {
        permissions.add(perm);
    }

    @Override
    public void revokePermission(String perm) {
        permissions.remove(perm);
    }

    @Override
    public boolean holdsPermission(String perm) {
        return permissions.contains(perm);
    }

    class PlayerSpeedImpl implements PlayerSpeed {
        @Override
        public float flyingSpeed() {
            return abilities.flySpeed;
        }

        @Override
        public void setFlyingSpeed(float flyingSpeed) {
            abilities.flySpeed = flyingSpeed;
        }

        @Override
        public float sneakSpeed() {
            TridentLogger.error(new UnsupportedOperationException("You may not get the sneak speed of an OfflinePlayer!"));
            return -1;
        }

        @Override
        public void setSneakSpeed(float speed) {
            TridentLogger.error(new UnsupportedOperationException("You may not set the sneak speed of an OfflinePlayer!"));
        }

        @Override
        public float walkSpeed() {
            return abilities.walkingSpeed;
        }

        @Override
        public void setWalkSpeed(float speed) {
            abilities.walkingSpeed = speed;
        }
    }
}
