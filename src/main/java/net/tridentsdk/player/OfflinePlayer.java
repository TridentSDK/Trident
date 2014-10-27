/*
 * Copyright (c) 2014, The TridentSDK Team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     1. Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 *     2. Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 *     3. Neither the name of TridentSDK nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.tridentsdk.player;

import net.tridentsdk.api.GameMode;
import net.tridentsdk.api.Location;
import net.tridentsdk.api.entity.Entity;
import net.tridentsdk.api.entity.EntityProperties;
import net.tridentsdk.api.entity.Projectile;
import net.tridentsdk.api.entity.living.Player;
import net.tridentsdk.api.event.entity.EntityDamageEvent;
import net.tridentsdk.api.inventory.Inventory;
import net.tridentsdk.api.inventory.ItemStack;
import net.tridentsdk.api.nbt.*;
import net.tridentsdk.api.threads.TaskExecutor;
import net.tridentsdk.api.world.Dimension;
import net.tridentsdk.data.Slot;
import net.tridentsdk.entity.TridentInventoryHolder;
import net.tridentsdk.server.TridentServer;
import net.tridentsdk.world.TridentWorld;

import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListSet;

public class OfflinePlayer extends TridentInventoryHolder implements Player {

    private static final Set<OfflinePlayer> players = new ConcurrentSkipListSet<>();

    private final TaskExecutor executor = TridentServer.getInstance().provideThreads().providePlayerThread(this);

    protected Dimension dimesion;
    protected GameMode gameMode;
    protected int score;
    protected short selectedSlot;
    protected Location spawnLocation;
    protected short hunger;
    protected float exhaustion;
    protected float saturation;
    protected int foodTickTimer;
    protected int xpLevel;
    protected float xpPercent;
    protected int xpTotal;
    protected int xpSeed;
    protected Inventory enderChest;
    protected final PlayerAbilities abilities = new PlayerAbilities();

    public OfflinePlayer(CompoundTag tag, TridentWorld world) {
        super(null, null);

        load(tag);

        dimesion = Dimension.getDimension(((IntTag) tag.getTag("Dimension")).getValue());
        gameMode = GameMode.getGameMode(((IntTag) tag.getTag("playerGameType")).getValue());
        score = ((IntTag) tag.getTag("Score")).getValue();
        selectedSlot = (short) ((IntTag) tag.getTag("SelectedItemSlot")).getValue();

        if(tag.containsTag("SpawnX")) {
            spawnLocation = new Location(world, ((IntTag) tag.getTag("SpawnX")).getValue(),
                    ((IntTag) tag.getTag("SpawnY")).getValue(),
                    ((IntTag) tag.getTag("SpawnZ")).getValue());
        } else {
            spawnLocation = null; // TODO: set it to world's spawn location
        }

        hunger = (short) ((IntTag) tag.getTag("foodLevel")).getValue();
        exhaustion = ((FloatTag) tag.getTag("foodExhaustionLevel")).getValue();
        saturation = ((FloatTag) tag.getTag("foodSaturationLevel")).getValue();
        foodTickTimer = ((IntTag) tag.getTag("foodTickTimer")).getValue();
        xpLevel = ((IntTag) tag.getTag("XpLevel")).getValue();
        xpPercent = ((IntTag) tag.getTag("XpP")).getValue();
        xpTotal = ((IntTag) tag.getTag("XpLevel")).getValue();
        xpSeed = ((IntTag) tag.getTag("XpSeed")).getValue();

        for(NBTTag t : ((ListTag) tag.getTag("Inventory")).listTags()) {
            Slot slot = NBTSerializer.deserialize(Slot.class, (CompoundTag) t);

            inventory.setSlot(slot.getSlot(), slot.toItemStack());
        }

        for(NBTTag t : ((ListTag) tag.getTag("EnderItems")).listTags()) {
            Slot slot = NBTSerializer.deserialize(Slot.class, (CompoundTag) t);

            enderChest.setSlot(slot.getSlot(), slot.toItemStack());
        }

        NBTSerializer.deserialize(abilities, (CompoundTag) tag.getTag("abilities"));
        players.add(this);
    }

    public static OfflinePlayer getOfflinePlayer(UUID id) {
        for(OfflinePlayer player : players) {
            if(player.getUniqueId().equals(id)) {
                return player;
            }
        }

        return null;
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

        tag.addTag(NBTSerializer.serialize(new Slot(getItemInHand())));
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

        for(ItemStack is : inventory.getContents()) {
            inventoryTag.addTag(NBTSerializer.serialize(new Slot(is)));
        }

        tag.addTag(inventoryTag);

        ListTag enderTag = new ListTag("EnderItems", TagType.COMPOUND);

        for(ItemStack is : enderChest.getContents()) {
            enderTag.addTag(NBTSerializer.serialize(new Slot(is)));
        }

        tag.addTag(enderTag);

        tag.addTag(NBTSerializer.serialize(abilities, "abilities"));

        return tag;
    }
}
