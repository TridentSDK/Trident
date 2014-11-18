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
import net.tridentsdk.api.world.World;
import net.tridentsdk.server.packets.play.out.PacketPlayOutPlayerRespawn;
import net.tridentsdk.server.packets.play.out.PacketPlayOutStatistics;
import net.tridentsdk.server.player.PlayerConnection;
import net.tridentsdk.server.player.TridentPlayer;
import net.tridentsdk.server.netty.ClientConnection;
import net.tridentsdk.server.netty.packet.InPacket;
import net.tridentsdk.server.netty.packet.Packet;
import net.tridentsdk.server.world.TridentWorld;

/**
 * Sent by the client when it's ready to login or respawn after death
 */
public class PacketPlayInClientStatus extends InPacket {

    /**
     * Action ID values: <p/> 0 - Perform Respawn 1 - Request statistics 2 - Open inventory acheivement
     */
    protected short actionId;

    @Override
    public int getId() {
        return 0x15;
    }

    public short getActionId() {
        return this.actionId;
    }

    @Override
    public Packet decode(ByteBuf buf) {
        this.actionId = buf.readUnsignedByte();

        return this;
    }

    @Override
    public void handleReceived(ClientConnection connection) {
        TridentPlayer player = ((PlayerConnection) connection).getPlayer();
        World world = player.getWorld();
        StatusType type = StatusType.getStatus((int) this.actionId);

        switch (type) {
            case RESPAWN:
                PacketPlayOutPlayerRespawn respawn = new PacketPlayOutPlayerRespawn();

                respawn.set("dimesion", (int) ((TridentWorld) world).getDimesion().toByte())
                        .set("difficulity", (int) world.getDifficulty().toByte())
                        .set("gameMode", (int) world.getDefaultGamemode().toByte()
                        /* todo make this specific to the player */);

                connection.sendPacket(respawn);
                break;

            case STATISTICS_REQUEST:
                PacketPlayOutStatistics statistics = new PacketPlayOutStatistics();

                // TODO prepare statistics for the player
                statistics.set("entries", null);

                connection.sendPacket(statistics); // inb4 NPE
                break;

            case OPEN_INVENTORY_ACHEIVEMENT:
                // no packet existing for this, are we missing said packet?

                break;

            default:
                throw new IllegalArgumentException("Client sent invalid status, maybe modified?"); // catched by
                // PacketHandler
        }
    }

    public enum StatusType {
        RESPAWN(0),
        STATISTICS_REQUEST(1),
        OPEN_INVENTORY_ACHEIVEMENT(2);

        private final int id;

        StatusType(int id) {
            this.id = id;
        }

        public static StatusType getStatus(int id) {
            for (StatusType type : StatusType.values()) {
                if (type.getId() == id)
                    return type;
            }

            return null;
        }

        public int getId() {
            return this.id;
        }
    }
}
