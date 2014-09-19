/*
 * Copyright (c) 2014, The TridentSDK Team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     1. Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 *     2. Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 *     3. Neither the name of TridentSDK nor the names of its
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

package net.tridentsdk.player;

import net.tridentsdk.api.Location;
import net.tridentsdk.api.entity.living.Player;
import net.tridentsdk.entity.TridentInventoryHolder;
import net.tridentsdk.entity.TridentLivingEntity;
import net.tridentsdk.packets.play.out.PacketPlayOutChatMessage;
import net.tridentsdk.packets.play.out.PacketPlayOutDisconnect;
import net.tridentsdk.packets.play.out.PacketPlayOutKeepAlive;
import net.tridentsdk.server.netty.client.ClientConnection;
import net.tridentsdk.server.netty.packet.OutPacket;

import java.util.UUID;

public abstract class TridentPlayer extends TridentInventoryHolder implements Player { // abstract for now to avoid compilation errors

    private static final TridentPlayer[] players = {};

    private final PlayerConnection connection;

    public TridentPlayer(UUID uniqueId, Location spawnLocation, ClientConnection connection) {
        super(uniqueId, spawnLocation);

        this.connection = new PlayerConnection(connection, this);
    }

    @Override
    public void tick() {
        super.tick();

        if(connection.getKeepAliveId() == (-1)) {
            // send Keep Alive packet if not sent already
            PacketPlayOutKeepAlive packet = new PacketPlayOutKeepAlive();

            connection.sendPacket(packet);
            connection.setKeepAliveId(packet.getKeepAliveId(), ticksExisted);
        }else if((ticksExisted - connection.getKeepAliveSent()) >= 600L){
            // kick the player for not responding to the keep alive within 30 seconds/600 ticks
            kickPlayer("Timed out!");
        }
    }

    public void sendMessage(String message) {
        connection.sendPacket(new PacketPlayOutChatMessage().set("jsonMessage", message).set("position",
                PacketPlayOutChatMessage.ChatPosition.CHAT));
    }

    /*
     * @NotJavaDoc
     * TODO: Create Message API and utilize it
     */
    public void kickPlayer(String reason) {
        connection.sendPacket(new PacketPlayOutDisconnect().set("reason", reason));
        connection.logout();
    }

    public PlayerConnection getConnection() {
        return connection;
    }

    public static void sendAll(OutPacket packet) {
        for(TridentPlayer p : players) {
            p.connection.sendPacket(packet, true);
        }
    }
}
