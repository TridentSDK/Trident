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
package net.tridentsdk.packets.play.in;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.player.PlayerConnection;
import net.tridentsdk.player.TridentPlayer;
import net.tridentsdk.server.netty.ClientConnection;
import net.tridentsdk.server.netty.packet.InPacket;
import net.tridentsdk.server.netty.packet.Packet;

/**
 * Packet is sent when the player starts/stops flying with the second parameter changed accordingly. All other
 * parameters are ignored by the vanilla server.
 */
public class PacketPlayInPlayerAbilities extends InPacket {

    /**
     * The flags are whether damage is disabled (god mode, 8, bit 3), whether the player can fly (4, bit 2), whether the
     * player is flying (2, bit 1), and whether the player is in creative mode (1, bit 0). <p/> To get the values of
     * these booleans, simply AND (&) the byte with 1,2,4 and 8 respectively, to get the 0 or 1 bitwise value. To set
     * them OR (|) them with their repspective masks.
     */
    protected byte flags;

    /**
     * Previous integer value divided by 250
     */
    protected float flyingSpeed;
    /**
     * Previous integer value divided by 250
     */
    protected float walkingSpeed;

    @Override
    public int getId() {
        return 0x13;
    }

    @Override
    public Packet decode(ByteBuf buf) {
        this.flags = buf.readByte();

        this.flyingSpeed = buf.readFloat();
        this.walkingSpeed = buf.readFloat();

        return this;
    }

    public byte getFlags() {
        return this.flags;
    }

    public float getFlyingSpeed() {
        return this.flyingSpeed;
    }

    public float getWalkingSpeed() {
        return this.walkingSpeed;
    }

    @Override
    public void handleReceived(ClientConnection connection) {
        TridentPlayer player = ((PlayerConnection) connection).getPlayer();

        if (player.getFlyingSpeed() * 250.0F != this.flyingSpeed) {
            throw new IllegalArgumentException("Client sent invalid flying speed, possibly hack installed");
        }

        // TODO: act accordingly
    }
}
