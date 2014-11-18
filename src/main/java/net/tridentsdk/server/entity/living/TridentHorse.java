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
package net.tridentsdk.server.entity.living;

import net.tridentsdk.api.Location;
import net.tridentsdk.api.entity.*;
import net.tridentsdk.api.entity.living.Horse;
import net.tridentsdk.api.entity.living.Player;
import net.tridentsdk.api.event.entity.EntityDamageEvent;
import net.tridentsdk.api.inventory.Inventory;
import net.tridentsdk.api.inventory.ItemStack;
import net.tridentsdk.server.entity.TridentLivingEntity;

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
