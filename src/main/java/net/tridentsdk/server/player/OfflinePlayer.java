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

import io.netty.util.internal.chmv8.ConcurrentHashMapV8;
import net.tridentsdk.Coordinates;
import net.tridentsdk.GameMode;
import net.tridentsdk.entity.Entity;
import net.tridentsdk.entity.EntityProperties;
import net.tridentsdk.entity.living.Player;
import net.tridentsdk.entity.projectile.Projectile;
import net.tridentsdk.event.entity.EntityDamageEvent;
import net.tridentsdk.meta.nbt.*;
import net.tridentsdk.server.TridentServer;
import net.tridentsdk.server.data.Slot;
import net.tridentsdk.server.entity.TridentInventoryHolder;
import net.tridentsdk.server.world.TridentWorld;
import net.tridentsdk.util.TridentLogger;
import net.tridentsdk.window.inventory.Inventory;
import net.tridentsdk.window.inventory.Item;
import net.tridentsdk.world.Dimension;
import net.tridentsdk.world.World;

import javax.annotation.concurrent.ThreadSafe;
import java.util.Collections;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@ThreadSafe public class OfflinePlayer extends TridentInventoryHolder implements Player {
    private static final Set<OfflinePlayer> players = Collections.newSetFromMap(
            new ConcurrentHashMapV8<OfflinePlayer, Boolean>());

    protected String name;
    protected Dimension dimension;
    protected GameMode gameMode;
    protected int score;
    protected short selectedSlot;
    protected Coordinates spawnLocation;
    protected short hunger;
    protected float exhaustion;
    protected float saturation;
    protected int foodTickTimer;
    protected int xpLevel;
    protected float xpPercent;
    protected int xpTotal;
    protected int xpSeed;
    protected Inventory enderChest;
    protected PlayerAbilities abilities = new PlayerAbilities();

    public OfflinePlayer(CompoundTag tag, TridentWorld world) {
        super(null, world.spawnLocation());

        load(tag);

        dimension = Dimension.getDimension(((IntTag) tag.getTag("Dimension")).getValue());
        gameMode = GameMode.gamemodeOf(((IntTag) tag.getTag("playerGameType")).getValue());
        score = ((IntTag) tag.getTag("Score")).getValue();
        selectedSlot = (short) ((IntTag) tag.getTag("SelectedItemSlot")).getValue();

        if (tag.containsTag("SpawnX")) {
            spawnLocation = Coordinates.create(world, ((IntTag) tag.getTag("SpawnX")).getValue(),
                                               ((IntTag) tag.getTag("SpawnY")).getValue(),
                                               ((IntTag) tag.getTag("SpawnZ")).getValue());
        } else {
            spawnLocation = world.spawnLocation();
        }

        hunger = (short) ((IntTag) tag.getTag("foodLevel")).getValue();
        exhaustion = ((FloatTag) tag.getTag("foodExhaustionLevel")).getValue();
        saturation = ((FloatTag) tag.getTag("foodSaturationLevel")).getValue();
        foodTickTimer = ((IntTag) tag.getTag("foodTickTimer")).getValue();
        xpLevel = ((IntTag) tag.getTag("XpLevel")).getValue();
        xpPercent = ((IntTag) tag.getTag("XpP")).getValue();
        xpTotal = ((IntTag) tag.getTag("XpLevel")).getValue();
        xpSeed = ((IntTag) tag.getTag("XpSeed")).getValue();

        for (NBTTag t : ((ListTag) tag.getTag("Inventory")).listTags()) {
            Slot slot = NBTSerializer.deserialize(Slot.class, (CompoundTag) t);

            //inventory.setSlot(slot.getSlot(), slot.toItemStack());
        }

        for (NBTTag t : ((ListTag) tag.getTag("EnderItems")).listTags()) {
            Slot slot = NBTSerializer.deserialize(Slot.class, (CompoundTag) t);

            //enderChest.setSlot(slot.getSlot(), slot.toItemStack());
        }

        NBTSerializer.deserialize(abilities, (CompoundTag) tag.getTag("abilities"));
        players.add(this);
    }

    public static OfflinePlayer getOfflinePlayer(UUID id) {
        for (OfflinePlayer player : players) {
            if (player.getUniqueId().equals(id)) {
                return player;
            }
        }

        return null;
    }

    public static CompoundTag generatePlayer(UUID id) {
        World defaultWorld = TridentServer.WORLD;
        Coordinates spawnLocation = defaultWorld.spawnLocation();
        CompoundTagBuilder<NBTBuilder> builder = NBTBuilder.newBase(id.toString());

        builder.stringTag("id", String.valueOf(counter.incrementAndGet()));
        builder.longTag("UUIDMost", id.getMostSignificantBits());
        builder.longTag("UUIDLeast", id.getLeastSignificantBits());

        ListTagBuilder<CompoundTagBuilder<NBTBuilder>> pos = builder.beginListTag("Pos", TagType.INT);

        pos.tag((int) spawnLocation.getX());
        pos.tag((int) spawnLocation.getY());
        pos.tag((int) spawnLocation.getZ());

        builder = pos.endListTag();

        ListTagBuilder<CompoundTagBuilder<NBTBuilder>> motion = builder.beginListTag("Motion", TagType.INT);

        motion.tag(0);
        motion.tag(0);
        motion.tag(0);

        builder = motion.endListTag();

        ListTagBuilder<CompoundTagBuilder<NBTBuilder>> rotation = builder.beginListTag("Rotation", TagType.INT);

        rotation.tag(0);
        rotation.tag(0);

        builder = rotation.endListTag();

        builder.floatTag("FallDistance", 0);
        builder.shortTag("Fire", (short) 0);
        builder.shortTag("Air", (short) 0);

        builder.byteTag("OnGround", (byte) 1);
        builder.byteTag("Invulnerable", (byte) 0);

        builder.intTag("Dimension", Dimension.OVERWORLD.toByte());
        builder.intTag("PortalCooldown", 900);

        builder.stringTag("CustomName", "");
        builder.byteTag("CustomNameVisible", (byte) 0);

        builder.byteTag("Silent", (byte) 0);

        builder.compoundTag(new CompoundTag("Riding"));

        builder.intTag("Dimension", Dimension.OVERWORLD.toByte());
        builder.intTag("playerGameType", GameMode.SURVIVAL.toByte());
        builder.intTag("Score", 0);
        builder.intTag("SelectedGameSlot", 0);

        builder.intTag("foodLevel", 20);
        builder.floatTag("foodExhaustionLevel", 0F);
        builder.floatTag("foodSaturationLevel", 0F);
        builder.intTag("foodTickTimer", 0);

        builder.intTag("XpLevel", 0);
        builder.intTag("XpP", 0);
        builder.intTag("XpLevel", 0);
        builder.intTag("XpSeed", 0); // actually give a proper seed

        builder.listTag(new ListTag("Inventory", TagType.COMPOUND));
        builder.listTag(new ListTag("EnderItems", TagType.COMPOUND));

        builder.intTag("SelectedItemSlot", 0);

        builder.compoundTag(NBTSerializer.serialize(new PlayerAbilities(), "abilities"));

        return builder.endCompoundTag().build();
    }

    public Coordinates getSpawnLocation() {
        return spawnLocation;
    }

    @Override
    public float getFlyingSpeed() {
        return abilities.flySpeed;
    }

    @Override
    public void setFlyingSpeed(float flyingSpeed) {
        TridentLogger.error(new UnsupportedOperationException("You may not set the flying speed of an OfflinePlayer!"));
    }

    @Override
    public Locale getLocale() {
        return null;
    }

    @Override
    public Item getHeldItem() {
        return inventory.getItems()[selectedSlot + 36];
    }

    @Override
    public GameMode getGameMode() {
        return gameMode;
    }

    @Override
    public float getMoveSpeed() {
        return 0;
    }

    @Override
    public void setMoveSpeed(float speed) {

    }

    @Override
    public float getSneakSpeed() {
        return 0;
    }

    @Override
    public void setSneakSpeed(float speed) {

    }

    @Override
    public float getWalkSpeed() {
        return abilities.walkingSpeed;
    }

    @Override
    public void setWalkSpeed(float speed) {
        TridentLogger.error(
                new UnsupportedOperationException("You may not set the walking speed of an OfflinePlayer!"));
    }

    @Override
    public void sendMessage(String message) {
        TridentLogger.error(new UnsupportedOperationException("You can't send messages to a non-existant player"));
    }

    @Override
    public void invokeCommand(String message) {
        TridentLogger.error(new UnsupportedOperationException("You cannot make an OfflinePlayer invoke a command!"));
    }

    @Override
    public String getLastCommand() {
        return null;
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
    public EntityDamageEvent getLastDamageCause() {
        return null;
    }

    @Override
    public Player hurtByPlayer() {
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
    public <T extends Projectile> T launchProjectile(EntityProperties properties) {
        TridentLogger.error(new UnsupportedOperationException("You cannot make an OfflinePlayer launch a projectile!"));
        return null;
    }

    public CompoundTag toNbt() {
        CompoundTag tag = new CompoundTag(getUniqueId().toString());

        tag.addTag(new IntTag("Dimension").setValue(dimension.toByte()));
        tag.addTag(new IntTag("playerGameType").setValue(gameMode.toByte()));
        tag.addTag(new IntTag("Score").setValue(score));
        tag.addTag(new IntTag("SelectedItemSlot").setValue(selectedSlot));

        //tag.addTag(NBTSerializer.serialize(new Slot(getItemInHand())));
        tag.addTag(new IntTag("SpawnX").setValue((int) spawnLocation.getX()));
        tag.addTag(new IntTag("SpawnY").setValue((int) spawnLocation.getY()));
        tag.addTag(new IntTag("SpawnZ").setValue((int) spawnLocation.getZ()));

        tag.addTag(new ShortTag("foodLevel").setValue(hunger));
        tag.addTag(new FloatTag("foodExhaustionLevel").setValue(exhaustion));
        tag.addTag(new FloatTag("foodSaturationLevel").setValue(saturation));
        tag.addTag(new IntTag("footTickTimer").setValue(foodTickTimer));

        tag.addTag(new IntTag("XpLevel").setValue(xpLevel));
        tag.addTag(new FloatTag("XpP").setValue(xpPercent));
        tag.addTag(new IntTag("XpTotal").setValue(xpTotal));
        tag.addTag(new IntTag("XpSeed").setValue(xpSeed));

        ListTag inventoryTag = new ListTag("Inventory", TagType.COMPOUND);

        /*for (ItemStack is : inventory.getItems()) {
            inventoryTag.addTag(NBTSerializer.serialize(new Slot(is)));
        }*/

        tag.addTag(inventoryTag);

        ListTag enderTag = new ListTag("EnderItems", TagType.COMPOUND);

        /*for (ItemStack is : enderChest.getItems()) {
            enderTag.addTag(NBTSerializer.serialize(new Slot(is)));
        }*/

        tag.addTag(enderTag);
        tag.addTag(NBTSerializer.serialize(abilities, "abilities"));

        return tag;
    }
}
