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
import net.tridentsdk.GameMode;
import net.tridentsdk.Handler;
import net.tridentsdk.event.player.PlayerToggleFlyingEvent;
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
     * The flags are whether damage is disabled (god mode, 8, bit 3), whether the player can fly (4, bit 2), whether
     * the
     * player is flying (2, bit 1), and whether the player is in creative mode (1, bit 0).  To get the values of
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
    public int id() {
        return 0x13;
    }

    @Override
    public Packet decode(ByteBuf buf) {
        this.flags = buf.readByte();

        this.flyingSpeed = buf.readFloat();
        this.walkingSpeed = buf.readFloat();

        return this;
    }

    public byte flags() {
        return this.flags;
    }

    public float flyingSpeed() {
        return this.flyingSpeed;
    }

    public float walkingSpeed() {
        return this.walkingSpeed;
    }

    @Override
    public void handleReceived(ClientConnection connection) {
        TridentPlayer player = ((PlayerConnection) connection).player();

        if (player.speedModifiers().flyingSpeed() * 250.0F != this.flyingSpeed) {
            TridentLogger.error(
                    new IllegalArgumentException("Client sent invalid flying speed, possibly hack installed"));
            // ((PlayerConnection) connection).player().kickPlayer(
            //        new MessageBuilder("You flew too quickly").build().asJson());
        }

        boolean flying = (byte) (flags & 2) == 2;

        if(player.gameMode() == GameMode.CREATIVE || flying != player.isFlying()) {
            PlayerToggleFlyingEvent toggleFly = new PlayerToggleFlyingEvent(player, flying);

            Handler.forEvents().fire(toggleFly);

            player.setFlying(flying);
        }

        // TODO: act accordingly
    }
}
