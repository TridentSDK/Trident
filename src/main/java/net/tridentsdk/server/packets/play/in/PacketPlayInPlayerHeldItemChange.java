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
package net.tridentsdk.server.packets.play.in;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.server.netty.ClientConnection;
import net.tridentsdk.server.netty.packet.InPacket;
import net.tridentsdk.server.netty.packet.Packet;
import net.tridentsdk.server.player.PlayerConnection;

/**
 * Packet sent when player changes selected slot
 */
public class PacketPlayInPlayerHeldItemChange extends InPacket {

    /**
     * Slot number from 0-8
     */
    protected short slot;

    @Override
    public int getId() {
        return 0x09;
    }

    @Override
    public Packet decode(ByteBuf buf) {
        this.slot = buf.readShort();

        return this;
    }

    public short getSlot() {
        return this.slot;
    }

    @Override
    public void handleReceived(ClientConnection connection) {
        ((PlayerConnection) connection).getPlayer().setSlot(this.slot);
    }
}
