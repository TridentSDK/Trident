/*
 * Copyright (c) 2014, TridentSDK Team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * * Neither the name of TridentSDK nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */


package net.tridentsdk.packets.play.in;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.api.world.World;
import net.tridentsdk.packets.play.out.PacketPlayOutPlayerRespawn;
import net.tridentsdk.packets.play.out.PacketPlayOutStatistics;
import net.tridentsdk.player.PlayerConnection;
import net.tridentsdk.player.TridentPlayer;
import net.tridentsdk.server.netty.ClientConnection;
import net.tridentsdk.server.netty.packet.InPacket;
import net.tridentsdk.server.netty.packet.Packet;

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
        StatusType type = StatusType.getStatus(actionId);

        switch(type) {
            case RESPAWN:
                PacketPlayOutPlayerRespawn respawn = new PacketPlayOutPlayerRespawn();

                respawn.set("dimesion", (int) world.getDimesion().toByte())
                        .set("difficulity", (int) world.getDifficulity().toByte())
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
                throw new IllegalArgumentException("Client sent invalid status, maybe modified?"); // catched by PacketHandler
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

        public int getId() {
            return this.id;
        }

        public static StatusType getStatus(int id) {
            for(StatusType type : values()) {
                if(type.getId() == id)
                    return type;
            }

            return null;
        }
    }
}
