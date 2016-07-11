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
import net.tridentsdk.entity.living.Horse;
import net.tridentsdk.entity.living.Player;
import net.tridentsdk.entity.types.EntityType;
import net.tridentsdk.entity.types.HorseType;
import net.tridentsdk.entity.types.HorseVariant;
import net.tridentsdk.event.entity.EntityDamageEvent;
import net.tridentsdk.inventory.Inventory;
import net.tridentsdk.inventory.Item;
import net.tridentsdk.server.data.MetadataType;
import net.tridentsdk.server.data.ProtocolMetadata;
import net.tridentsdk.server.entity.TridentBreedable;
import net.tridentsdk.server.player.TridentPlayer;

import java.util.UUID;

public class TridentHorse extends TridentBreedable implements Horse {
    private volatile int data;
    private final HorseType breed;
    private volatile int colorData;
    private volatile UUID owner;
    private volatile int temper;
    private volatile int armorType;

    public TridentHorse(UUID id, Position spawnLocation, HorseType breed) {
        super(id, spawnLocation);

        this.data = 0;
        this.breed = breed;
        this.colorData = 0;
        this.temper = 0;
        this.armorType = 0;
    }

    @Override
    protected void doEncodeMeta(ProtocolMetadata protocolMeta) {
        //protocolMeta.setMeta(16, MetadataType.INT, data); TODO Fix
        protocolMeta.setMeta(19, MetadataType.BYTE, (byte) breed.id());
        //protocolMeta.setMeta(20, MetadataType.INT, colorData); TODO Fix
        protocolMeta.setMeta(21, MetadataType.STRING,
                (owner == null) ? "" : TridentPlayer.getPlayer(owner).name());
        //protocolMeta.setMeta(22, MetadataType.INT, armorType); TODO Fix
    }

    @Override
    public HorseType breed() {
        return breed;
    }

    @Override
    public boolean isGrazing() {
        return false;
    }

    @Override
    public int temper() {
        return temper;
    }

    @Override
    public boolean hasChest() {
        return false;
    }

    @Override
    public HorseVariant variant() {
        return null;
    }

    @Override
    public boolean isSitting() {
        return false;
    }

    @Override
    public Inventory window() {
        return null;
    }

    @Override
    public Item heldItem() {
        return null;
    }

    @Override
    public void setHeldItem(Item item) {

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
    public UUID owner() {
        return null;
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
    public EntityType type() {
        return EntityType.HORSE;
    }
}
