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


import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.concurrent.ThreadSafe;

import net.tridentsdk.GameMode;
import net.tridentsdk.Position;
import net.tridentsdk.Trident;
import net.tridentsdk.entity.Entity;
import net.tridentsdk.entity.Projectile;
import net.tridentsdk.entity.living.Player;
import net.tridentsdk.entity.traits.EntityProperties;
import net.tridentsdk.entity.traits.PlayerSpeed;
import net.tridentsdk.event.entity.EntityDamageEvent;
import net.tridentsdk.factory.Factories;
import net.tridentsdk.meta.nbt.ByteTag;
import net.tridentsdk.meta.nbt.CompoundTag;
import net.tridentsdk.meta.nbt.CompoundTagBuilder;
import net.tridentsdk.meta.nbt.DoubleTag;
import net.tridentsdk.meta.nbt.FloatTag;
import net.tridentsdk.meta.nbt.IntTag;
import net.tridentsdk.meta.nbt.ListTag;
import net.tridentsdk.meta.nbt.ListTagBuilder;
import net.tridentsdk.meta.nbt.LongTag;
import net.tridentsdk.meta.nbt.NBTBuilder;
import net.tridentsdk.meta.nbt.NBTSerializer;
import net.tridentsdk.meta.nbt.NBTTag;
import net.tridentsdk.meta.nbt.ShortTag;
import net.tridentsdk.meta.nbt.TagType;
import net.tridentsdk.permission.Permission;
import net.tridentsdk.server.TridentServer;
import net.tridentsdk.server.data.Slot;
import net.tridentsdk.server.entity.TridentInventoryHolder;
import net.tridentsdk.server.window.TridentWindow;
import net.tridentsdk.server.world.TridentWorld;
import net.tridentsdk.util.TridentLogger;
import net.tridentsdk.window.inventory.Inventory;
import net.tridentsdk.world.Dimension;
import net.tridentsdk.world.World;

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
    protected final Set<Permission> permissions = Factories.collect().createSet();

    OfflinePlayer(UUID uuid, CompoundTag tag, TridentWorld world) {
        super(uuid, world.getSpawnPosition());

        load(tag);

        dimension = Dimension.getDimension(((IntTag) tag.getTag("Dimension")).getValue());
        gameMode = GameMode.getById(((IntTag) tag.getTag("playerGameType")).getValue());
        score = ((IntTag) tag.getTag("Score")).getValue();
        selectedSlot = (short) ((IntTag) tag.getTag("SelectedItemSlot")).getValue();

        if (tag.containsTag("SpawnX")) {
            spawnLocation = Position.create(world, ((IntTag) tag.getTag("SpawnX")).getValue(),
                    ((IntTag) tag.getTag("SpawnY")).getValue(), ((IntTag) tag.getTag("SpawnZ")).getValue());
        } else {
            spawnLocation = world.getSpawnPosition();
        }

        hunger = (short) ((IntTag) tag.getTag("foodLevel")).getValue();
        exhaustion = ((FloatTag) tag.getTag("foodExhaustionLevel")).getValue();
        saturation = ((FloatTag) tag.getTag("foodSaturationLevel")).getValue();
        foodTickTimer = ((IntTag) tag.getTag("foodTickTimer")).getValue();
        xpLevel = ((IntTag) tag.getTag("XpLevel")).getValue();
        xpPercent = ((FloatTag) tag.getTag("XpP")).getValue();
        xpTotal = ((IntTag) tag.getTag("XpLevel")).getValue();
        xpSeed = tag.containsTag("XpSeed") ? ((IntTag) tag.getTag("XpSeed")).getValue() :
                new IntTag("XpSeed").setValue(0).getValue();

        // TODO come up with a valid implementation of this...?
        inventory = new TridentWindow(45);
        for (NBTTag t : ((ListTag) tag.getTag("Inventory")).listTags()) {
            Slot slot = NBTSerializer.deserialize(Slot.class, (CompoundTag) t);

            //inventory.setSlot(slot.getSlot(), slot.toItemStack());
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
        Position spawnLocation = defaultWorld.getSpawnPosition();
        CompoundTagBuilder<NBTBuilder> builder = NBTBuilder.newBase(id.toString());

        builder.stringTag("id", String.valueOf(counter.incrementAndGet()));
        builder.longTag("UUIDMost", id.getMostSignificantBits());
        builder.longTag("UUIDLeast", id.getLeastSignificantBits());

        ListTagBuilder<CompoundTagBuilder<NBTBuilder>> pos = builder.beginListTag("Pos", TagType.DOUBLE);

        pos.tag(spawnLocation.getX());
        pos.tag(spawnLocation.getY());
        pos.tag(spawnLocation.getZ());

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
        builder.intTag("playerGameType", Trident.getServer().getConfig().getByte("default-gamemode"));
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
    public Locale getLocale() {
        return null;
    }

    @Override
    public GameMode getGameMode() {
        return gameMode;
    }

    @Override
    public void setGameMode(GameMode mode) {
        this.gameMode = mode;
        this.abilities.creative = (byte) ((mode == GameMode.CREATIVE) ? 1 : 0);
        this.abilities.canFly =  (byte) ((mode == GameMode.CREATIVE) ? 1 : 0);
    }

    @Override
    public PlayerSpeed getSpeedModifiers() {
        return playerSpeed;
    }

    @Override
    public void sendMessage(String message) {
        TridentLogger.error(new UnsupportedOperationException("You can't send messages to a non-existant player"));
    }

    @Override
    public void runCommand(String message) {
        TridentLogger.error(new UnsupportedOperationException("You cannot make an OfflinePlayer invoke a command!"));
    }

    @Override
    public String getLastCommand() {
        return null;
    }

    @Override
    public boolean isOperator() {
        // DEBUG ===== Everyone is OP'd!
        return true;
        // =====
    }
    
    @Override
    public void setOperator(boolean op) {
    	// NYI
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
    public EntityDamageEvent getLastDamageEvent() {
        return null;
    }

    @Override
    public Player getLastPlayerDamager() {
        return null;
    }

    @Override
    public void sendRaw(String... messages) {
        TridentLogger.error(new UnsupportedOperationException("You cannot send a message to an OfflinePlayer!"));
    }

    @Override
    public String getLastMessage() {
        return null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public <T extends Projectile> T launchProjectile(EntityProperties properties) {
        TridentLogger.error(new UnsupportedOperationException("You cannot make an OfflinePlayer launch a projectile!"));
        return null;
    }

    public CompoundTag asNbt() {
        CompoundTag tag = new CompoundTag(getUniqueId().toString());

        tag.addTag(new LongTag("UUIDMost").setValue(uniqueId.getMostSignificantBits()));
        tag.addTag(new LongTag("UUIDLeast").setValue(uniqueId.getLeastSignificantBits()));

        tag.addTag(new IntTag("Dimension").setValue(dimension.asByte()));
        tag.addTag(new IntTag("playerGameType").setValue(gameMode.asByte()));
        tag.addTag(new IntTag("Score").setValue(score));
        tag.addTag(new IntTag("SelectedItemSlot").setValue(selectedSlot));

        //tag.addTag(NBTSerializer.serialize(new Slot(itemInHand())));
        tag.addTag(new IntTag("SpawnX").setValue((int) spawnLocation.getX()));
        tag.addTag(new IntTag("SpawnY").setValue((int) spawnLocation.getY()));
        tag.addTag(new IntTag("SpawnZ").setValue((int) spawnLocation.getZ()));

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
        position.addTag(new DoubleTag("").setValue(loc.getX()));
        position.addTag(new DoubleTag("").setValue(loc.getY()));
        position.addTag(new DoubleTag("").setValue(loc.getZ()));

        tag.addTag(position);

        ListTag motion = new ListTag("Motion",TagType.DOUBLE) ;
        motion.addTag(new DoubleTag("").setValue(velocity.getX()));
        motion.addTag(new DoubleTag("").setValue(velocity.getY()));
        motion.addTag(new DoubleTag("").setValue(velocity.getZ()));

        tag.addTag(motion);

        ListTag rotation = new ListTag("Rotation", TagType.FLOAT);
        rotation.addTag(new FloatTag("").setValue(loc.getYaw()));
        rotation.addTag(new FloatTag("").setValue(loc.getPitch()));

        tag.addTag(rotation);

        ListTag inventoryTag = new ListTag("Inventory", TagType.COMPOUND);

        /*for (ItemStack is : inventory.items()) {
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
    public void setPermission(Permission permission, boolean enabled) {
        if (enabled && !hasPermission(permission)) {
        	permissions.add(permission);
        } else if (hasPermission(permission) && !enabled) {
        	permissions.remove(permission);
        }
    }

    @Override
    public boolean hasPermission(Permission perm) {
        return permissions.contains(perm);
    }

    class PlayerSpeedImpl implements PlayerSpeed {
        @Override
        public float getFlyingSpeed() {
            return abilities.flySpeed;
        }

        @Override
        public void setFlyingSpeed(float flyingSpeed) {
            abilities.flySpeed = flyingSpeed;
        }

        @Override
        public float getSneakSpeed() {
            TridentLogger.error(new UnsupportedOperationException("You may not get the sneak speed of an OfflinePlayer!"));
            return -1;
        }

        @Override
        public void setSneakSpeed(float speed) {
            TridentLogger.error(new UnsupportedOperationException("You may not set the sneak speed of an OfflinePlayer!"));
        }

        @Override
        public float setWalkSpeed() {
            return abilities.walkingSpeed;
        }

        @Override
        public void setWalkSpeed(float speed) {
            abilities.walkingSpeed = speed;
        }
    }
}
