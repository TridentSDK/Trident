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

package net.tridentsdk.server.entity.decorate;

import net.tridentsdk.entity.Entity;
import net.tridentsdk.entity.LivingEntity;
import net.tridentsdk.entity.VillagerCareer;
import net.tridentsdk.window.inventory.InventoryType;

public final class Decorator {
    private Decorator() {
    }

    public static DecoratedAgeable asAgeable(LivingEntity entity, final boolean canBreed, final boolean inLove) {
        return new DecoratedAgeable(entity) {
            @Override
            public boolean canBreed() {
                return canBreed;
            }

            @Override
            public boolean isInLove() {
                return inLove;
            }
        };
    }

    public static DecoratedEquippable asEquippable(LivingEntity entity) {
        return new DecoratedEquippable(entity);
    }

    public static DecoratedHostile asHostile(LivingEntity entity, boolean apply) {
        DecoratedHostile hostile = new DecoratedHostile(entity);
        if (apply) hostile.applyHostilityUpdate();
        return hostile;
    }

    public static DecoratedInventoryHolder newInventoryHolder(Entity entity, String string, int size, InventoryType
            type) {
        return new DecoratedInventoryHolder(entity, string, size, type);
    }

    public static DecoratedInventoryHolder newInventory(String string, int size) {
        return newInventoryHolder(null, string, size, InventoryType.CHEST);
    }

    public static DecoratedInventoryHolder newInventory(String string, int size, InventoryType type) {
        return newInventoryHolder(null, string, size, type);
    }

    public static DecoratedNeutral asNeutral(LivingEntity entity) {
        return new DecoratedNeutral(entity);
    }

    public static DecoratedPeaceful asPeaceful(LivingEntity entity, boolean apply) {
        DecoratedPeaceful peaceful = new DecoratedPeaceful(entity);
        if (apply) peaceful.applyPeaceUpdate();
        return peaceful;
    }

    public static DecoratedTameable asTameable(LivingEntity entity, boolean canBreed, boolean isInLove) {
        return new DecoratedTameable(entity, canBreed, isInLove);
    }

    public static DecoratedTameable asTameable(LivingEntity entity) {
        return asTameable(entity, false, false);
    }

    public static DecoratedTradable asTradable(VillagerCareer career) {
        DecoratedTradable tradable = new DecoratedTradable();
        if (career != null) tradable.applyUpdateTrades(career);
        return tradable;
    }
}
