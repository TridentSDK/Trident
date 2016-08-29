/*
 * Trident - A Multithreaded Server Alternative
 * Copyright 2016 The TridentSDK Team
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
import lombok.Getter;
import net.tridentsdk.chat.ChatComponent;
import net.tridentsdk.chat.ChatType;
import net.tridentsdk.server.packet.PacketOut;

import static net.tridentsdk.server.net.NetData.wstr;

@Getter
public class PlayOutChat extends PacketOut {

    private ChatComponent chat;
    private ChatType type;

    public PlayOutChat(ChatComponent chat, ChatType type) {
        super(PlayOutChat.class);
        this.chat = chat;
        this.type = type;
    }

    @Override
    public void write(ByteBuf buf) {
        wstr(buf, chat.toString());
        buf.writeByte(type.ordinal());
    }

}
