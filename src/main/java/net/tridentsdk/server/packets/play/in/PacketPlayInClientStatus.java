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
import net.tridentsdk.server.packets.play.out.PacketPlayOutPlayerRespawn;
import net.tridentsdk.server.packets.play.out.PacketPlayOutStatistics;
import net.tridentsdk.server.player.PlayerConnection;
import net.tridentsdk.server.player.TridentPlayer;
import net.tridentsdk.util.TridentLogger;
import net.tridentsdk.world.World;

/**
 * Sent by the client when it's ready to login or respawn after death
 */
public class PacketPlayInClientStatus extends InPacket {

    /**
     * Action ID values:  0 - Perform Respawn 1 - Request statistics 2 - Open inventory acheivement
     */
    protected short actionId;

    @Override
    public int id() {
        return 0x16;
    }

    public short actionId() {
        return this.actionId;
    }

    @Override
    public Packet decode(ByteBuf buf) {
        this.actionId = buf.readUnsignedByte();

        return this;
    }

    @Override
    public void handleReceived(ClientConnection connection) {
        TridentPlayer player = ((PlayerConnection) connection).player();
        World world = player.world();
        StatusType type = StatusType.getStatus((int) this.actionId);

        switch (type) {
            case RESPAWN:
                PacketPlayOutPlayerRespawn respawn = new PacketPlayOutPlayerRespawn();

                respawn.set("dimension", (int) world.settings().dimension().asByte())
                        .set("difficulity", (int) world.settings().difficulty().asByte())
                        .set("gameMode", (int) world.settings().defaultGameMode().asByte()
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
                TridentLogger.get().error(
                        new IllegalArgumentException("Client sent invalid status, maybe modified?")); // catched by
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
