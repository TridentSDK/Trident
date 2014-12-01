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
package net.tridentsdk.packets.play.in;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.api.Location;
import net.tridentsdk.api.event.player.PlayerMoveEvent;
import net.tridentsdk.packets.play.out.PacketPlayOutEntityCompleteMove;
import net.tridentsdk.packets.play.out.PacketPlayOutEntityTeleport;
import net.tridentsdk.player.PlayerConnection;
import net.tridentsdk.player.TridentPlayer;
import net.tridentsdk.server.TridentServer;
import net.tridentsdk.server.netty.ClientConnection;
import net.tridentsdk.server.netty.packet.InPacket;
import net.tridentsdk.server.netty.packet.Packet;

/**
 * This packet is sent when the player wishes updates the player's XYZ position on the server.
 */
public class PacketPlayInPlayerMove extends InPacket {

    /**
     * Updated location, Y is the feet location
     */
    protected Location location;
    /**
     * Wether the player is on the ground or not
     */
    protected boolean onGround;

    @Override
    public int getId() {
        return 0x04;
    }

    @Override
    public Packet decode(ByteBuf buf) {
        double x = buf.readDouble();
        double y = buf.readDouble();
        double z = buf.readDouble();

        this.location = new Location(null, x, y, z); // TODO: Get the player's world

        this.onGround = buf.readBoolean();

        return this;
    }

    public Location getLocation() {
        return this.location;
    }

    public boolean isOnGround() {
        return this.onGround;
    }

    @Override
    public void handleReceived(ClientConnection connection) {
        TridentPlayer player = ((PlayerConnection) connection).getPlayer();
        this.location.setWorld(player.getWorld());
        Location from = player.getLocation();
        Location to = this.location;

        PlayerMoveEvent event = new PlayerMoveEvent(player, from, to);

        TridentServer.getInstance().getEventManager().call(event);

        if (event.isIgnored()) {
            PacketPlayOutEntityTeleport cancel = new PacketPlayOutEntityTeleport();

            cancel.set("entityId", player.getId())
                    .set("location", from)
                    .set("onGround", player.isOnGround());

            TridentPlayer.sendAll(cancel);
            return;
        }

        player.setLocation(to);

        Packet move = new PacketPlayOutEntityCompleteMove();

        // set fields

        TridentPlayer.sendAll(move);
    }
}
