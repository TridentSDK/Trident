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
package net.tridentsdk.impl.packets.play.in;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.api.Location;
import net.tridentsdk.api.event.player.PlayerMoveEvent;
import net.tridentsdk.impl.packets.play.out.PacketPlayOutEntityLook;
import net.tridentsdk.impl.packets.play.out.PacketPlayOutEntityTeleport;
import net.tridentsdk.impl.player.PlayerConnection;
import net.tridentsdk.impl.player.TridentPlayer;
import net.tridentsdk.impl.TridentServer;
import net.tridentsdk.impl.netty.ClientConnection;
import net.tridentsdk.impl.netty.packet.InPacket;
import net.tridentsdk.impl.netty.packet.Packet;

/**
 * Packet sent when only Yaw + Pitch is sent by the client
 */
public class PacketPlayInPlayerLook extends InPacket {

    /**
     * Absolute rotation on the X Axis, in degrees
     */
    protected float newYaw;
    /**
     * Absolute rotation on the Y Axis, in degrees
     */
    protected float newPitch;

    /**
     * Wether the playeris on the ground or not
     */
    protected boolean onGround;

    @Override
    public int getId() {
        return 0x05;
    }

    @Override
    public Packet decode(ByteBuf buf) {
        this.newYaw = buf.readFloat();
        this.newPitch = buf.readFloat();
        this.onGround = buf.readBoolean();

        return this;
    }

    @Override
    public void handleReceived(ClientConnection connection) {
        TridentPlayer player = ((PlayerConnection) connection).getPlayer();
        Location from = player.getLocation();
        Location to = player.getLocation();

        to.setYaw(this.newYaw);
        to.setPitch(this.newPitch);

        PlayerMoveEvent event = new PlayerMoveEvent(player, from, to);

        TridentServer.getInstance().getEventManager().call(event);

        if (event.isCancelled()) {
            PacketPlayOutEntityTeleport cancel = new PacketPlayOutEntityTeleport();

            cancel.set("entityId", player.getId())
                    .set("location", from)
                    .set("onGround", player.isOnGround());

            TridentPlayer.sendAll(cancel);
            return;
        }

        player.setLocation(to);

        PacketPlayOutEntityLook headMove = new PacketPlayOutEntityLook();

        headMove.set("entityId", player.getId())
                .set("location", to)
                .set("onGround", player.isOnGround());

        TridentPlayer.sendAll(headMove);
    }
}
