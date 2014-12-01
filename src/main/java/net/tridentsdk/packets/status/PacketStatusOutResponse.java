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

    String version = "1.8";
    int protocol = 47;
    String maxPlayers = "10";
    String onlinePlayers = "5";
    String description = "motd";

    @Override
    public int getId() {
        return 0x00;
    }

    @Override
    public void encode(ByteBuf buf) {
        String json = BASE_DATA.replaceAll("${version.name}", version);
        json = json.replaceAll("${version.protocol}", protocol); //Maybe change to get StatusIn's protocol if it's 4, 5, or 47?
        json = json.replaceAll("${players.max}", maxPlayers); //Hey woah, this is now a STRING! Maybe we can implement this in the Plugin API somehow? *wink wink* *nudge nudge* 
        json = json.replaceAll("${players.online}", onlinePlayers); // ^^
        json = json.replaceAll("${description.text}", description); //Not quite sure if this is the acutal MOTD that is read, or a filler
        Codec.writeString(buf, json);
    }
}
