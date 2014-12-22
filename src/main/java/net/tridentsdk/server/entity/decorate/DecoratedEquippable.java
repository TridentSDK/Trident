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

import net.tridentsdk.entity.LivingEntity;
import net.tridentsdk.entity.decorate.Equippable;
import net.tridentsdk.entity.decorate.LivingDecorationAdapter;
import net.tridentsdk.server.packets.play.out.PacketPlayOutEntityEquipment;
import net.tridentsdk.server.player.TridentPlayer;
import net.tridentsdk.window.inventory.Item;

public class DecoratedEquippable extends LivingDecorationAdapter implements Equippable {
    private Item[] equipment = new Item[4];

    protected DecoratedEquippable(LivingEntity entity) {
        super(entity);
    }

    @Override
    public Item[] getEquipment() {
        return equipment;
    }

    @Override
    public void setEquipment(Item[] stack) {
        this.equipment = stack;
    }

    public void applyArmorUpdate() {
        for (int i = 0; i < equipment.length; i++) {
            Item stack = equipment[i];
            PacketPlayOutEntityEquipment entityEquipment = new PacketPlayOutEntityEquipment();
            entityEquipment
                    .set("entityId", original().getId())
                    .set("slot", (short) i + 5)
                    .set(String.valueOf(i + 5),
                            Long.decode(Integer.toHexString(stack.getId()) + "010000ffff"));
            TridentPlayer.sendAll(entityEquipment);
        }
    }
}
