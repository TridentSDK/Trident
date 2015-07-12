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
import net.tridentsdk.Position;
import net.tridentsdk.base.Substance;
import net.tridentsdk.server.netty.ClientConnection;
import net.tridentsdk.server.netty.packet.InPacket;
import net.tridentsdk.server.netty.packet.Packet;
import net.tridentsdk.server.player.PlayerConnection;
import net.tridentsdk.server.player.TridentPlayer;
import net.tridentsdk.util.Vector;

public class PacketPlayInBlockPlace extends InPacket {
    /**
     * Location of the block being placed
     */
    protected Position location;
    protected byte direction; // wat
    /**
     * PositionWritable of the cursor, incorrect use of a Vector
     */
    protected Vector cursorPosition;

    @Override
    public int id() {
        return 0x08;
    }

    @Override
    public Packet decode(ByteBuf buf) {
        long encodedLocation = buf.readLong();

        this.location = Position.create(null, (double) (encodedLocation >> 38),
                (double) ((encodedLocation >> 26) & 0xFFF), (double) (encodedLocation << 38 >> 38));
        this.direction = buf.readByte();

        // ignore held item
        for (int i = 0; i < buf.readableBytes() - 3; i++) {
            buf.readByte();
        }

        double x = (double) buf.readByte();
        double y = (double) buf.readByte();
        double z = (double) buf.readByte();

        this.cursorPosition = new Vector(x, y, z);
        return this;
    }

    public Position location() {
        return this.location;
    }

    public byte blockDirection() {
        return this.direction;
    }

    public Vector cursorPosition() {
        return this.cursorPosition;
    }

    @Override
    public void handleReceived(ClientConnection connection) {
        TridentPlayer player = ((PlayerConnection) connection).player();

        location.setWorld(player.world());

        Substance substance = player.heldItem().type();
        if (substance != Substance.AIR) {
            location().block().setSubstance(substance);
        }

        System.out.printf("Is he at (%s, %s, %s)\n", player.position().x(), player.position().y(), player.position().z());
    }
}
