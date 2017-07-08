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
import net.tridentsdk.server.inventory.TridentInventory;
import net.tridentsdk.server.net.Slot;
import net.tridentsdk.server.packet.PacketOut;

import javax.annotation.concurrent.Immutable;

/**
 * Sent to the client for whenever the server wants to
 * populate the opened window with items.
 */
@Immutable
public class PlayOutWindowItems extends PacketOut {
    /**
     * The inventory to obtain the items to send
     */
    private final TridentInventory inventory;

    public PlayOutWindowItems(TridentInventory inventory) {
        super(PlayOutWindowItems.class);
        this.inventory = inventory;
    }

    @Override
    public void write(ByteBuf buf) {
        buf.writeByte(this.inventory.getId());
        buf.writeShort(this.inventory.getSize());
        for (int i = 0; i < this.inventory.getSize(); i++) {
            Slot.newSlot(this.inventory.get(i)).write(buf);
        }
    }
}