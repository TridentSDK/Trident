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

public class PacketPlayInPlayerConfirmTransaction extends InPacket {

    /**
     * Each action number is unique
     */
    protected short actionNumber;
    /**
     * Wether the transaction was accepted
     */
    protected boolean accepted;

    @Override
    public int getId() {
        return 0x0F;
    }

    @Override
    public Packet decode(ByteBuf buf) {
        buf.readByte(); //ignore windowId, we'd have the window the player has open anyhow

        this.actionNumber = buf.readShort();
        this.accepted = buf.readBoolean();

        return this;
    }

    public short getActionNumber() {
        return this.actionNumber;
    }

    public boolean isAccepted() {
        return this.accepted;
    }

    @Override
    public void handleReceived(ClientConnection connection) {
        // TODO: Act accordingly
    }
}
