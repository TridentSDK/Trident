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
import net.tridentsdk.server.netty.ClientConnection;
import net.tridentsdk.server.netty.Codec;
import net.tridentsdk.server.netty.packet.InPacket;
import net.tridentsdk.server.netty.packet.Packet;

/**
 * Sent by the client when doing any of the action types below. <p/> Note: Client will send ActionType#START_SPRINTING
 * when "Leave bed" is clicked
 *
 * @see ActionType
 */
public class PacketPlayInEntityAction extends InPacket {

    /**
     * Entity ActionType
     *
     * @see ActionType
     */
    protected ActionType type;
    /**
     * Horse jump boost, mentioned if not ActionType#ON_HOURSE
     *
     * @see ActionType#ON_HORSE
     */
    protected int jumpBoost;

    @Override
    public int getId() {
        return 0x0B;
    }

    @Override
    public Packet decode(ByteBuf buf) {
        Codec.readVarInt32(buf); // ignore entity id as its the player's
        this.type = ActionType.getAction((int) buf.readUnsignedByte());
        this.jumpBoost = Codec.readVarInt32(buf);

        return this;
    }

    @Override
    public void handleReceived(ClientConnection connection) {
        // TODO: Act accordingly
    }

    public enum ActionType {
        CROUCH(0),
        UN_CROUCH(1),
        LEAVE_BED(2),
        START_SPRINTING(3),
        STOP_SPRINTING(4),
        ON_HORSE(5),
        OPEN_INVENTORY(6);

        /**
         * Id used to identify the action type
         */
        protected final int id;

        ActionType(int id) {
            this.id = id;
        }

        public static ActionType getAction(int id) {
            for (ActionType type : ActionType.values()) {
                if (type.id == id)
                    return type;
            }

            throw new IllegalArgumentException(id + " is not a valid ActionType id!");
        }

        public int getId() {
            return this.id;
        }
    }
}
