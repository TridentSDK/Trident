/*
 *     Trident - A Multithreaded Server Alternative
 *     Copyright (C) 2014, The TridentSDK Team
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.tridentsdk.packets.status;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.server.netty.Codec;
import net.tridentsdk.server.netty.packet.OutPacket;

/**
 * Status response to PacketStatusInRequest
 *
 * @author The TridentSDK Team
 * @see net.tridentsdk.packets.status.PacketStatusInRequest
 */
public class PacketStatusOutResponse extends OutPacket {

    //May be a lot of text, but saves the creation of 4 Objects with 5 variables & no GsonBuilders    
    public static final String BASE_DATA = "{\"version\":{\"name\":\"${version.name}\",\"protocol\":${version.protocol}},\"players\":{\"max\":${players.max},\"online\":${players.online}},\"description\":{\"text\": \"${description.text}\"},\"favicon\":\"\"}";

    @Override
    public int getId() {
        return 0x00;
    }

    @Override
    public void encode(ByteBuf buf) {
        String json = BASE_DATA.replaceAll("${version.name}", "1.8");
        json = json.replaceAll("${version.protocol}", 47); //Maybe change to get StatusIn's protocol if it's 4, 5, or 47?
        json = json.replaceAll("${players.max}", "10"); //Hey woah, this is now a STRING! Maybe we can implement this in the Plugin API somehow? *wink wink* *nudge nudge* 
        json = json.replaceAll("${players.online}", "5"); // ^^
        json = json.replaceAll("${description.text}", "motd"); //Not quite sure if this is the acutal MOTD that is read, or a filler
        Codec.writeString(buf, json);
    }
}
