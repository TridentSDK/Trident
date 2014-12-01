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
package net.tridentsdk.entity.living;

import net.tridentsdk.api.Location;
import net.tridentsdk.api.entity.*;
import net.tridentsdk.api.entity.living.Horse;
import net.tridentsdk.api.entity.living.Player;
import net.tridentsdk.api.event.entity.EntityDamageEvent;
import net.tridentsdk.api.inventory.Inventory;
import net.tridentsdk.api.inventory.ItemStack;
import net.tridentsdk.entity.TridentLivingEntity;

import java.util.UUID;

public class TridentHorse extends TridentLivingEntity implements Horse {

    public TridentHorse(UUID id, Location spawnLocation) {
        super(id, spawnLocation);
    }

    @Override
    public HorseType getBreed() {
        return null;
    }

    @Override
    public boolean isGrazing() {
        return false;
    }

    @Override
    public int getTemper() {
        return 0;
    }

    @Override
    public boolean hasChest() {
        return false;
    }

    @Override
    public HorseVariant getVariant() {
        return null;
    }

    @Override
    public boolean isSitting() {
        return false;
    }

    @Override
    public int getAge() {
        return 0;
    }

    @Override
    public void setAge(int ticks) {

    }

    @Override
    public boolean canBreed() {
        return false;
    }

    @Override
    public boolean isInLove() {
        return false;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }

    @Override
    public ItemStack getContent(int slot) {
        return null;
    }

    @Override
    public boolean isSaddled() {
        return false;
    }

    @Override
    public void setSaddled(boolean saddled) {

    }

    @Override
    public boolean isTamed() {
        return false;
    }

    @Override
    public UUID getOwner() {
        return null;
    }

    @Override
    public void hide(Entity entity) {

    }

    @Override
    public void show(Entity entity) {

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
    public boolean isNameVisible() {
        return false;
    }

    @Override
    public void applyProperties(EntityProperties properties) {

    }

    @Override
    public <T extends Projectile> T launchProjectile(EntityProperties properties) {
        return null;
    }
}
