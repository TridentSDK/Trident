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

package net.tridentsdk.server.entity.block;

import net.tridentsdk.Position;
import net.tridentsdk.entity.Entity;
import net.tridentsdk.entity.types.EntityType;
import net.tridentsdk.entity.block.ArmorStand;
import net.tridentsdk.entity.block.SlotProperties;
import net.tridentsdk.entity.living.Player;
import net.tridentsdk.event.entity.EntityDamageEvent;
import net.tridentsdk.server.data.MetadataType;
import net.tridentsdk.server.data.ProtocolMetadata;
import net.tridentsdk.server.entity.TridentLivingEntity;
import net.tridentsdk.util.PartRotation;
import net.tridentsdk.window.inventory.Item;

import java.util.UUID;

public class TridentArmorStand extends TridentLivingEntity implements ArmorStand {
    private final SlotProperties properties;
    /*
     * Data as represented in protocol meta, encoded as such to save space in memory
     *
     * At BitMask 1, it determines if its a small armor stand or not.
     * At BitMask 2, it states if gravity applies to this armor stand
     * At BitMask 4, it states if the armor stand "has" arms
     * At BitMask 8, it states if the armor stand "has" a baseplate
     */
    private volatile byte data;
    private final Item[] armor;
    private final PartRotation[] pose;

    public TridentArmorStand(UUID id, Position spawnLocation, SlotProperties properties) {
        super(id, spawnLocation);

        this.properties = properties;
        this.data = (byte) 14;
        this.armor = new Item[4];
        this.pose = new PartRotation[6];
    }

    @Override
    protected void doEncodeMeta(ProtocolMetadata protocolMeta) {
        protocolMeta.setMeta(10, MetadataType.BYTE, data);

        for (int i = 1; i <= 6; i++) {
            protocolMeta.setMeta(10 + i, MetadataType.PYR, pose[i - 1].asVector());
        }
    }

    @Override
    public SlotProperties getSlotProperties() {
        return properties;
    }

    @Override
    public boolean isInvisible() {
        return false;
    }

    @Override
    public boolean isBaseplateEnabled() {
        return (data & 8) == 8;
    }

    @Override
    public boolean isArmsEnabled() {
        return (data & 4) == 4;
    }

    @Override
    public boolean useGravity() {
        return (data & 2) == 2;
    }

    @Override
    public PartRotation[] getPose() {
        return pose;
    }

    @Override
    public boolean isTiny() {
        return (data & 1) == 1;
    }

    @Override
    public Item[] getEquipment() {
        return armor;
    }

    @Override
    public void setEquipment(final Item[] stack) {
        System.arraycopy(stack, 0, armor, 0, (stack.length > 4) ? 4 : stack.length);
    }

    @Override
    public void hide(Entity entity) {
    }

    @Override
    public void show(Entity entity) {
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
    public EntityType getType() {
        return EntityType.ARMOR_STAND;
    }
}
