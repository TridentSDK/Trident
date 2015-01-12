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
import net.tridentsdk.Coordinates;
import net.tridentsdk.server.netty.ClientConnection;
import net.tridentsdk.server.netty.Codec;
import net.tridentsdk.server.netty.packet.InPacket;
import net.tridentsdk.server.netty.packet.Packet;

/**
 * Packet is sent when a player wishes to update a sign
 */
public class PacketPlayInUpdateSign extends InPacket {
    /**
     * Contents of the sign, represented in JSON
     */
    protected final String[] jsonContents = new String[4];
    /**
     * Location of the sign
     */
    protected Coordinates signLocation;

    @Override
    public int getId() {
        return 0x12;
    }

    @Override
    public Packet decode(ByteBuf buf) {
        long encoded = buf.readLong();
        double x = (double) (encoded >> 38);
        double y = (double) (encoded << 26 >> 52);
        double z = (double) (encoded << 38 >> 38);

        this.signLocation = Coordinates.create(null, x, y, z);

        for (int i = 0; i <= 4; i++) {
            this.jsonContents[i] = Codec.readString(buf);
        }
        return this;
    }

    public Coordinates getSignLocation() {
        return this.signLocation;
    }

    public String[] getJsonContents() {
        return this.jsonContents;
    }

    @Override
    public void handleReceived(ClientConnection connection) {
        // TODO: Act accordingly (reminder: update world)
    }
}
