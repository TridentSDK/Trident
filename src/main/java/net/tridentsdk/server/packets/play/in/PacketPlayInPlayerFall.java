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
import net.tridentsdk.base.Substance;
import net.tridentsdk.server.netty.ClientConnection;
import net.tridentsdk.server.netty.packet.InPacket;
import net.tridentsdk.server.netty.packet.Packet;
import net.tridentsdk.server.player.PlayerConnection;
import net.tridentsdk.server.player.TridentPlayer;
import net.tridentsdk.util.Vector;

/**
 * This packet is used to indicate whether the player is on ground (walking/swimming), or airborne (jumping/falling).
 */
public class PacketPlayInPlayerFall extends InPacket {
    /**
     * True if the client is on the ground, False otherwise
     */
    protected boolean onGround;

    @Override
    public int id() {
        return 0x03;
    }

    @Override
    public Packet decode(ByteBuf buf) {
        this.onGround = buf.readBoolean();

        return this;
    }

    public boolean isOnGround() {
        return this.onGround;
    }

    @Override
    public void handleReceived(ClientConnection connection) {
        TridentPlayer player = ((PlayerConnection) connection).player();
        player.setOnGround(onGround);
       /* TODO: See if this is actually needed
        if (player.position().add(new Vector(0, -0.1, 0)).block().substance() != Substance.AIR
                && player.isFlying()) {
            player.setFlying(false);
        }
       */
    }
}
