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
package net.tridentsdk.server.entity.living;

import net.tridentsdk.base.Position;
import net.tridentsdk.entity.living.Player;
import net.tridentsdk.entity.living.Villager;
import net.tridentsdk.entity.types.EntityType;
import net.tridentsdk.entity.types.VillagerCareer;
import net.tridentsdk.entity.types.VillagerProfession;
import net.tridentsdk.event.entity.EntityDamageEvent;
import net.tridentsdk.inventory.trade.Trade;
import net.tridentsdk.server.entity.TridentLivingEntity;

import java.util.Collection;
import java.util.UUID;

/**
 * Represents a Villager
 *
 * @author The TridentSDK Team
 */
public class TridentVillager extends TridentLivingEntity implements Villager {
    private volatile VillagerCareer career;
    private volatile VillagerProfession role;

    public TridentVillager(UUID uuid, Position spawnPosition, VillagerCareer career, VillagerProfession role) {
        super(uuid, spawnPosition);
        this.career = career;
        this.role = role;
    }

    @Override
    public VillagerProfession profession() {
        return role;
    }

    @Override
    public void setProfession(VillagerProfession profession) {
        this.role = profession;
    }

    @Override
    public VillagerCareer career() {
        return career;
    }

    @Override
    public void setCareer(VillagerCareer career) {
        this.career = career;
    }

    @Override
    public int careerLevel() {
        return 0;
    }

    @Override
    public int age() {
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
    public EntityDamageEvent lastDamageEvent() {
        return null;
    }

    @Override
    public Player lastPlayerDamager() {
        return null;
    }

    @Override
    public Collection<Trade> trades() {
        return null;
    }

    @Override
    public EntityType type() {
        return EntityType.VILLAGER;
    }
}
