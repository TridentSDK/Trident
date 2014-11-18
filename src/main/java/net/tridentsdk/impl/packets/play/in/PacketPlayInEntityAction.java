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
package net.tridentsdk.impl.packets.play.in;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.impl.netty.ClientConnection;
import net.tridentsdk.impl.netty.Codec;
import net.tridentsdk.impl.netty.packet.InPacket;
import net.tridentsdk.impl.netty.packet.Packet;

/**
 * Sent by the client when doing any of the action types below. <p/> Note: Client will send ActionType#START_SPRINTING
 * when "Leave bed" is clicked
 *
 * @see net.tridentsdk.impl.packets.play.in.PacketPlayInEntityAction.ActionType
 */
public class PacketPlayInEntityAction extends InPacket {

    /**
     * Entity ActionType
     *
     * @see net.tridentsdk.impl.packets.play.in.PacketPlayInEntityAction.ActionType
     */
    protected ActionType type;
    /**
     * Horse jump boost, mentioned if not ActionType#ON_HOURSE
     *
     * @see net.tridentsdk.impl.packets.play.in.PacketPlayInEntityAction.ActionType#ON_HORSE
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
