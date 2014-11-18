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
package net.tridentsdk.impl.packets.play.in;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.api.event.player.PlayerCloseWindowEvent;
import net.tridentsdk.impl.TridentServer;
import net.tridentsdk.impl.netty.ClientConnection;
import net.tridentsdk.impl.netty.packet.InPacket;
import net.tridentsdk.impl.netty.packet.Packet;

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
        PlayerCloseWindowEvent event = new PlayerCloseWindowEvent(TridentServer.getInstance().getWindow(this.id));

        TridentServer.getInstance().getEventManager().call(event);

        if (event.isCancelled()) {
            // force the window to be open

        }

        // process the closing of the window
    }
}
