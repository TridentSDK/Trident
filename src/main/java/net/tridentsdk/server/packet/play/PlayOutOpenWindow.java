/*
 * Trident - A Multithreaded Server Alternative
 * Copyright 2017 The TridentSDK Team
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
package net.tridentsdk.server.packet.play;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.entity.Entity;
import net.tridentsdk.inventory.InventoryType;
import net.tridentsdk.server.inventory.TridentInventory;
import net.tridentsdk.server.packet.PacketOut;

import javax.annotation.concurrent.Immutable;

import static net.tridentsdk.server.net.NetData.wstr;

/**
 * Opens an inventory window for the client.
 */
@Immutable
public class PlayOutOpenWindow extends PacketOut {
    /**
     * The inventory window to open
     */
    private final TridentInventory inventory;
    /**
     * The horse, if this inventory is a horse inventory
     */
    private final Entity entity;

    public PlayOutOpenWindow(TridentInventory inventory, Entity entity) {
        super(PlayOutOpenWindow.class);
        this.inventory = inventory;
        this.entity = entity;
    }

    @Override
    public void write(ByteBuf buf) {
        buf.writeByte(this.inventory.getId());
        wstr(buf, this.inventory.getType().toString());
        wstr(buf, this.inventory.getTitle().toString());
        buf.writeByte(this.inventory.getSize());

        if (this.inventory.getType() == InventoryType.HORSE) {
            buf.writeInt(this.entity.getId()); // y no varint, mojang
        }
    }
}
