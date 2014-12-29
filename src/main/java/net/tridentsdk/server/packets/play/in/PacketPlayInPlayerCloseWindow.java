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
import net.tridentsdk.event.player.PlayerCloseWindowEvent;
import net.tridentsdk.server.TridentServer;
import net.tridentsdk.server.netty.ClientConnection;
import net.tridentsdk.server.netty.packet.InPacket;
import net.tridentsdk.server.netty.packet.Packet;

/**
 * Packet sent by the client when closed a Window
 */
public class PacketPlayInPlayerCloseWindow extends InPacket {

    /**
     * Id of the window, 0 if player inventory
     */
    protected int id;

    @Override
    public int getId() {
        return 0x0D;
    }

    @Override
    public Packet decode(ByteBuf buf) {
        this.id = (int) buf.readByte();

        return this;
    }

    public int getWindowId() {
        return this.id;
    }

    @Override
    public void handleReceived(ClientConnection connection) {
        PlayerCloseWindowEvent event = new PlayerCloseWindowEvent(TridentServer.getInstance().windowBy(this.id));

        TridentServer.getInstance().eventHandler().call(event);

        if (event.isIgnored()) {
            // force the window to be open

        }

        // process the closing of the window
    }
}
