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

package net.tridentsdk.server.packets.play.out;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.server.netty.Codec;
import net.tridentsdk.server.netty.packet.OutPacket;

public class PacketPlayOutChatMessage extends OutPacket {

    protected String jsonMessage;
    protected ChatPosition position;

    @Override
    public int getId() {
        return 0x02;
    }

    @Override
    public void encode(ByteBuf buf) {
        Codec.writeString(buf, this.jsonMessage);
        buf.writeByte((int) this.position.toByte());
    }

    public enum ChatPosition {
        CHAT(0),
        SYSTEM_MESSAGE(1),
        ABOVE_BAR(2);

        protected final byte b;

        ChatPosition(int b) {
            this.b = (byte) b;
        }

        public byte toByte() {
            return this.b;
        }
    }
}
