/*
 * Copyright (C) 2014 The TridentSDK Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.tridentsdk.packets.status;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.server.netty.packet.OutPacket;
import net.tridentsdk.server.netty.packet.PacketType;

import java.io.UnsupportedEncodingException;

/**
 * TODO not an expert on this lol - AgentTroll
 *
 * @author The TridentSDK Team
 */
public class PacketStatusOutResponse extends OutPacket {
    private String jsonResponse;

    @Override
    public int getId() {
        return 0x00;
    }

    @Override
    public void encode(ByteBuf buf) {
        try {
            buf.writeBytes(this.jsonResponse.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException ignored) {
        }
    }

    @Override
    public PacketType getType() {
        return PacketType.OUT;
    }
}
