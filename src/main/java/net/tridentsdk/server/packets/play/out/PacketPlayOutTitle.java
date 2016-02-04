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

public class PacketPlayOutTitle extends OutPacket {

    protected int action;
    protected Object[] values;

    @Override
    public int id() {
        return 0x45;
    }

    public int action() {
        return this.action;
    }

    public Object[] values() {
        return this.values;
    }

    @Override
    public void encode(ByteBuf buf) {
        Codec.writeVarInt32(buf, this.action);

        for (Object o : this.values) {
            switch (o.getClass().getSimpleName()) {
                case "String":
                    Codec.writeString(buf, (String) o);
                    break;

                case "Integer":
                    buf.writeInt((Integer) o);
                    break;

                default:
                    // ignore bad developers
                    break;
            }
        }
    }

    public enum TitleAction {
        TITLE(0) {
            public String valueOf() { return "0"; }
        },
        SUBTITLE(1) {
            public String valueOf() { return "1"; }
        },
        TIMES_AND_DISPLAY(2) {
            public String valueOf() { return "2"; }
        },
        HIDE(3) {
            public String valueOf() { return "3"; }
        },
        RESET(4) {
            public String valueOf() { return "4"; }
        };

        private int id;

        TitleAction(int id) {
            this.id = id;
        }

        public int id() {
            return id;
        }
    }
}
