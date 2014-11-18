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
package net.tridentsdk.impl.packets.status;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.impl.netty.ClientConnection;
import net.tridentsdk.impl.netty.Codec;
import net.tridentsdk.impl.netty.packet.InPacket;
import net.tridentsdk.impl.netty.packet.Packet;
import net.tridentsdk.impl.netty.packet.PacketType;
import net.tridentsdk.impl.netty.protocol.Protocol;

/**
 * Represents a ping packet sent in from the client
 *
 * @author The TridentSDK Team
 */
public class PacketStatusInPing extends InPacket {
    /**
     * System time of the client (ms)
     */
    protected long time;

    @Override
    public int getId() {
        return 0x01;
    }

    @Override
    public Packet decode(ByteBuf buf) {
        this.time = Codec.readVarInt64(buf);

        return this;
    }

    @Override
    public void handleReceived(ClientConnection connection) {
        connection.sendPacket(new PacketStatusOutPing().set("clientTime", this.time));
        connection.setStage(Protocol.ClientStage.LOGIN);
    }

    /**
     * TODO not an expert on this lol - AgentTroll
     */
    public long getTime() {
        return this.time;
    }

    @Override
    public PacketType getType() {
        return PacketType.IN;
    }
}
