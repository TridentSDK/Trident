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
import net.tridentsdk.server.netty.packet.InPacket;
import net.tridentsdk.server.netty.packet.Packet;
import net.tridentsdk.server.player.PlayerConnection;
import net.tridentsdk.server.player.TridentPlayer;
import net.tridentsdk.util.TridentLogger;

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
            TridentLogger.error(new IllegalArgumentException("Client sent invalid flying speed, possibly hack installed"));
        }

        // TODO: act accordingly
    }
}
