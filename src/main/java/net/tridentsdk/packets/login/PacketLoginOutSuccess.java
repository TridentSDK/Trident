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

package net.tridentsdk.packets.login;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.api.Trident;
import net.tridentsdk.server.TridentServer;
import net.tridentsdk.server.netty.Codec;
import net.tridentsdk.server.netty.packet.OutPacket;
import net.tridentsdk.server.netty.packet.PacketType;

/**
 * TODO not an expert on this - AgentTroll
 *
 * @author The TridentSDK Team
 */
public class PacketLoginOutSuccess extends OutPacket {
    private String name;
    private String id;

    @Override
    public int getId() {
        return 0x02;
    }

    @Override
    public PacketType getType() {
        return PacketType.OUT;
    }

    public void setName(String name) {
        this.name = name;

        this.id = ((TridentServer) Trident.getServer()).getProfileRepository()
                                                       .findProfilesByNames(name)[0].getId();
    }

    @Override
    public void encode(ByteBuf buf) {
        Codec.writeString(buf, this.id);
        Codec.writeString(buf, this.name);
    }
}
