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
package net.tridentsdk.packets.play.in;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.server.netty.ClientConnection;
import net.tridentsdk.server.netty.Codec;
import net.tridentsdk.server.netty.packet.InPacket;
import net.tridentsdk.server.netty.packet.Packet;

/**
 * Sent by the client to state the status of downloading the resource pack
 */
public class PacketPlayInPackStatus extends InPacket {

    /**
     * Hash of the pack
     */
    protected String hash;
    /**
     * Result/Status <p/> 0 - Successfully loaded 1 - Declined 2 - Failed download 3 - Accepted <p/> TODO Change to
     * enum
     */
    protected int result;

    @Override
    public int getId() {
        return 0x19;
    }

    @Override
    public Packet decode(ByteBuf buf) {
        this.hash = Codec.readString(buf);
        this.result = Codec.readVarInt32(buf);

        return this;
    }

    public String getHash() {
        return this.hash;
    }

    public int getResult() {
        return this.result;
    }

    @Override
    public void handleReceived(ClientConnection connection) {
        // TODO: Act accordingly
    }
}
