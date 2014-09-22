/*
 * Copyright (c) 2014, The TridentSDK Team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     1. Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *     2. Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *     3. Neither the name of the The TridentSDK Team nor the
 *        names of its contributors may be used to endorse or promote products
 *        derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL The TridentSDK Team BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.tridentsdk.packets.play.out;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.server.netty.Codec;
import net.tridentsdk.server.netty.packet.OutPacket;

public class PacketPlayOutUpdateScore extends OutPacket {

    private String itemName;
    private UpdateType type; // TODO: Change to enum
    private String scoreName;
    private int value;

    @Override
    public int getId() {
        return 0x3C;
    }

    public String getItemName() {
        return this.itemName;
    }

    public UpdateType getUpdateType() {
        return this.type;
    }

    public String getScoreName() {
        return this.scoreName;
    }

    public int getValue() {
        return this.value;
    }

    @Override
    public void encode(ByteBuf buf) {
        Codec.writeString(buf, this.itemName);
        buf.writeByte((int) this.type.toByte());

        if (this.type.b == 1) {
            return;
        }

        Codec.writeString(buf, this.scoreName);
        Codec.writeVarInt32(buf, this.value);
    }

    public enum UpdateType {
        CREATE(0),
        UPDATE(0),
        REMOVE(1);

        private final byte b;

        UpdateType(int i) {
            this.b = (byte) i;
        }

        public byte toByte() {
            return this.b;
        }
    }
}
