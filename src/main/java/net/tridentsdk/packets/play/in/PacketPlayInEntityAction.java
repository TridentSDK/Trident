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

package net.tridentsdk.packets.play.in;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.server.netty.ClientConnection;
import net.tridentsdk.server.netty.Codec;
import net.tridentsdk.server.netty.packet.InPacket;
import net.tridentsdk.server.netty.packet.Packet;

/**
 * Sent by the client when doing any of the action types below.
 * <p/>
 * Note: Client will send ActionType#START_SPRINTING when "Leave bed" is clicked
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
    protected int jumpBoost; // because people at Mojang are fucking retards

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
