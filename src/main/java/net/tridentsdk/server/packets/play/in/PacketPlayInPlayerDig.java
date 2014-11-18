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
import net.tridentsdk.api.BlockFace;
import net.tridentsdk.api.Location;
import net.tridentsdk.api.event.Cancellable;
import net.tridentsdk.api.event.Event;
import net.tridentsdk.api.event.player.PlayerDigEvent;
import net.tridentsdk.api.event.player.PlayerDropItemEvent;
import net.tridentsdk.server.player.PlayerConnection;
import net.tridentsdk.server.player.TridentPlayer;
import net.tridentsdk.server.TridentServer;
import net.tridentsdk.server.netty.ClientConnection;
import net.tridentsdk.server.netty.packet.InPacket;
import net.tridentsdk.server.netty.packet.Packet;

public class PacketPlayInPlayerDig extends InPacket {
    private short status;
    private Location location;
    private short blockFace;

    @Override
    public int getId() {
        return 0x07;
    }

    public short getStatus() {
        return this.status;
    }

    public Location getLocation() {
        return this.location;
    }

    public short getBlockFace() {
        return this.blockFace;
    }

    @Override
    public Packet decode(ByteBuf buf) {
        this.status = (short) buf.readByte();
        long encodedLocation = buf.readLong();

        this.location = new Location(null, (double) (encodedLocation >> 38), (double) (encodedLocation << 26 >> 52),
                (double) (encodedLocation << 38 >> 38));
        this.blockFace = (short) buf.readByte();

        return this;
    }

    @Override
    public void handleReceived(ClientConnection connection) {
        TridentPlayer player = ((PlayerConnection) connection).getPlayer();
        DigStatus digStatus = DigStatus.getStatus(this.status);
        BlockFace face = null;

        switch (this.blockFace) {
            case 0:
                face = BlockFace.BOTTOM;
                break;

            case 1:
                face = BlockFace.TOP;
                break;

            case 2:
                // z--
                break;

            case 3:
                // z++
                break;

            case 4:
                // x--
                break;

            case 5:
                // x++
                break;

            default:
                throw new IllegalArgumentException("Client sent invalid BlockFace!");
        }

        Cancellable event = null;

        switch (digStatus) {
            case DIG_START:
            case DIG_CANCEL:
            case DIG_FINISH:
                event = new PlayerDigEvent(player, face, this.status);
                break;

            case DROP_ITEMSTACK:
                event = new PlayerDropItemEvent(player, null); // todo: spawn item and call the event
                break;

            case DROP_ITEM:
                event = new PlayerDropItemEvent(player, null);
                break;

            case SHOOT_ARROW:
                // shoot bow, if player has a food item finish eating
                break;
        }

        TridentServer.getInstance().getEventManager().call((Event) event);

        if (event == null || event.isCancelled())
            return;

        this.location.setWorld(player.getWorld());
    }

    public enum DigStatus {
        DIG_START(0),
        DIG_CANCEL(1),
        DIG_FINISH(2),
        DROP_ITEMSTACK(3),
        DROP_ITEM(4),
        SHOOT_ARROW(5);

        private final short id;

        DigStatus(int id) {
            this.id = (short) id;
        }

        public static DigStatus getStatus(short id) {
            for (DigStatus status : DigStatus.values()) {
                if (status.id == id) {
                    return status;
                }
            }

            return null;
        }

        public short getId() {
            return this.id;
        }
    }
}
