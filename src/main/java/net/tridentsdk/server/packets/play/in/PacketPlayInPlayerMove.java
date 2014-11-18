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
package net.tridentsdk.server.packets.play.in;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.api.Location;
import net.tridentsdk.api.event.player.PlayerMoveEvent;
import net.tridentsdk.server.packets.play.out.PacketPlayOutEntityCompleteMove;
import net.tridentsdk.server.packets.play.out.PacketPlayOutEntityTeleport;
import net.tridentsdk.server.player.PlayerConnection;
import net.tridentsdk.server.player.TridentPlayer;
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

        if (event.isCancelled()) {
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
