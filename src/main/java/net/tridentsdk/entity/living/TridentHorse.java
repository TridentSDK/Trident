/*
 * Copyright (c) 2014, TridentSDK Team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * * Neither the name of TridentSDK nor the names of its
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
package net.tridentsdk.entity.living;

import net.tridentsdk.api.Location;
import net.tridentsdk.api.entity.Entity;
import net.tridentsdk.api.entity.EntityProperties;
import net.tridentsdk.api.entity.HorseType;
import net.tridentsdk.api.entity.HorseVariant;
import net.tridentsdk.api.entity.Projectile;
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
