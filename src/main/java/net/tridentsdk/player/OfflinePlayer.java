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
package net.tridentsdk.player;

import io.netty.util.internal.ConcurrentSet;
import net.tridentsdk.api.GameMode;
import net.tridentsdk.api.Location;
import net.tridentsdk.api.Trident;
import net.tridentsdk.api.entity.Entity;
import net.tridentsdk.api.entity.EntityProperties;
import net.tridentsdk.api.entity.Projectile;
import net.tridentsdk.api.entity.living.Player;
import net.tridentsdk.api.event.entity.EntityDamageEvent;
import net.tridentsdk.api.factory.TridentFactory;
import net.tridentsdk.api.inventory.Inventory;
import net.tridentsdk.api.inventory.ItemStack;
import net.tridentsdk.api.nbt.*;
import net.tridentsdk.api.nbt.builder.CompoundTagBuilder;
import net.tridentsdk.api.nbt.builder.ListTagBuilder;
import net.tridentsdk.api.nbt.builder.NBTBuilder;
import net.tridentsdk.api.world.Dimension;
import net.tridentsdk.api.world.World;
import net.tridentsdk.data.Slot;
import net.tridentsdk.entity.TridentInventoryHolder;
import net.tridentsdk.world.TridentWorld;

import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public class OfflinePlayer extends TridentInventoryHolder implements Player {
    private static final Set<OfflinePlayer> players = new ConcurrentSet<>();

    protected String name;
    protected final Dimension dimesion;
    protected final GameMode gameMode;
    protected final int score;
    protected short selectedSlot;
    protected final Location spawnLocation;
    protected final short hunger;
    protected final float exhaustion;
    protected final float saturation;
    protected final int foodTickTimer;
    protected final int xpLevel;
    protected final float xpPercent;
    protected final int xpTotal;
    protected final int xpSeed;
    protected Inventory enderChest;
    protected final PlayerAbilities abilities = new PlayerAbilities();

    public OfflinePlayer(CompoundTag tag, TridentWorld world) {
        super(null, world.getSpawnLocation());

        load(tag);

        dimesion = Dimension.getDimension(((IntTag) tag.getTag("Dimension")).getValue());
        gameMode = GameMode.getGameMode(((IntTag) tag.getTag("playerGameType")).getValue());
        score = ((IntTag) tag.getTag("Score")).getValue();
        selectedSlot = (short) ((IntTag) tag.getTag("SelectedItemSlot")).getValue();

        if (tag.containsTag("SpawnX")) {
            spawnLocation = new Location(world, ((IntTag) tag.getTag("SpawnX")).getValue(),
                    ((IntTag) tag.getTag("SpawnY")).getValue(),
                    ((IntTag) tag.getTag("SpawnZ")).getValue());
        } else {
            spawnLocation = world.getSpawnLocation();
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

    public static CompoundTag generatePlayer(String name, UUID id) {
        World defaultWorld = Trident.getServer().getWorlds().iterator().next();
        Location spawnLocation = defaultWorld.getSpawnLocation();
        CompoundTagBuilder<NBTBuilder> builder = TridentFactory.createNbtBuilder("buttfuckery"); // because why the fuck not

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

    public Location getSpawnLocation() {
        return spawnLocation;
    }

    @Override
    public float getFlyingSpeed() {
        return abilities.flySpeed;
    }

    @Override
    public void setFlyingSpeed(float flyingSpeed) {
        throw new UnsupportedOperationException("You may not set the flying speed of an OfflinePlayer!");
    }

    @Override
    public Locale getLocale() {
        return null;
    }

    @Override
    public ItemStack getItemInHand() {
        return inventory.getContents()[selectedSlot + 36];
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
        throw new UnsupportedOperationException("You may not set the walking speed of an OfflinePlayer!");
    }

    @Override
    public void invokeCommand(String message) {
        throw new UnsupportedOperationException("You cannot make an OfflinePlayer invoke a command!");
    }

    @Override
    public String getLastCommand() {
        return null;
    }

    @Override
    public void hide(Entity entity) {
        throw new UnsupportedOperationException("You cannot hide an entity from an OfflinePlayer!");
    }

    @Override
    public void show(Entity entity) {
        throw new UnsupportedOperationException("You cannot show an entity to an OfflinePlayer!");
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
    public void sendMessage(String... messages) {
        throw new UnsupportedOperationException("You cannot send a message to an OfflinePlayer!");
    }

    @Override
    public String getLastMessage() {
        return null;
    }

    @Override
    public <T extends Projectile> T launchProjectile(EntityProperties properties) {
        throw new UnsupportedOperationException("You cannot make an OfflinePlayer launch a projectile!");
    }

    public CompoundTag toNbt() {
        CompoundTag tag = new CompoundTag(getUniqueId().toString());

        tag.addTag(new IntTag("Dimension").setValue(dimesion.toByte()));
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

        /*for (ItemStack is : inventory.getContents()) {
            inventoryTag.addTag(NBTSerializer.serialize(new Slot(is)));
        }*/

        tag.addTag(inventoryTag);

        ListTag enderTag = new ListTag("EnderItems", TagType.COMPOUND);

        /*for (ItemStack is : enderChest.getContents()) {
            enderTag.addTag(NBTSerializer.serialize(new Slot(is)));
        }*/

        tag.addTag(enderTag);
        tag.addTag(NBTSerializer.serialize(abilities, "abilities"));

        return tag;
    }
}
