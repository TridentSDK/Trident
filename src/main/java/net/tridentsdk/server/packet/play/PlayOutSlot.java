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
import net.tridentsdk.server.net.Slot;
import net.tridentsdk.server.packet.PacketOut;

import javax.annotation.concurrent.Immutable;

/**
 * Sets a slot in the given position of the given window
 * to the given slot data.
 */
@Immutable
public class PlayOutSlot extends PacketOut {
    /**
     * The window to set the slot, 0 for player inventory
     */
    private final int window;
    /**
     * The slot to place the item into
     */
    private final int pos;
    /**
     * The slot data
     */
    private final Slot slot;

    public PlayOutSlot(int window, int pos, Slot slot) {
        super(PlayOutSlot.class);
        this.window = window;
        this.pos = pos;
        this.slot = slot;
    }

    @Override
    public void write(ByteBuf buf) {
        buf.writeByte(this.window);
        buf.writeShort(this.pos);
        this.slot.write(buf);
    }
}