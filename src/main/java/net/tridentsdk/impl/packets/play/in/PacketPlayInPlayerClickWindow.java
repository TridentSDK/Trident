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
import net.tridentsdk.api.event.player.PlayerClickItemEvent;
import net.tridentsdk.api.window.Window;
import net.tridentsdk.impl.data.Slot;
import net.tridentsdk.impl.TridentServer;
import net.tridentsdk.impl.netty.ClientConnection;
import net.tridentsdk.impl.netty.packet.InPacket;
import net.tridentsdk.impl.netty.packet.Packet;

/**
 * Packet sent by the player when it clicks on a slot in a window.
 */
public class PacketPlayInPlayerClickWindow extends InPacket {

    /**
     * The id of the window which was clicked. 0 for player inventory.
     */
    protected int windowId;
    /**
     * The button used in the click, dependent on action number <p/> TODO reference to wiki
     */
    protected int clickedButton;

    /**
     * The clicked slot, -999 if not applicable
     */
    protected short clickedSlot;
    /**
     * A unique number for the action, used for transaction handling
     */
    protected short actionNumber;
    /**
     * Inventory operation mode
     */
    protected short mode;
    /**
     * Item clicked
     */
    protected Slot clickedItem;

    @Override
    public int getId() {
        return 0x0E;
    }

    public int getWindowId() {
        return this.windowId;
    }

    public int getClickedButton() {
        return this.clickedButton;
    }

    public short getClickedSlot() {
        return this.clickedSlot;
    }

    public short getActionNumber() {
        return this.actionNumber;
    }

    public short getMode() {
        return this.mode;
    }

    public Slot getClickedItem() {
        return this.clickedItem;
    }

    @Override
    public Packet decode(ByteBuf buf) {
        this.windowId = (int) buf.readByte();
        this.clickedSlot = buf.readShort();
        this.clickedButton = (int) buf.readByte();

        this.actionNumber = buf.readShort();
        this.mode = buf.readShort();
        this.clickedItem = new Slot(buf);

        return this;
    }

    @Override
    public void handleReceived(ClientConnection connection) {
        Window window = TridentServer.getInstance().getWindow(this.windowId);
        PlayerClickItemEvent clickEvent = new PlayerClickItemEvent(window, this.clickedSlot, (int) this.actionNumber);

        TridentServer.getInstance().getEventManager().call(clickEvent);

        if (clickEvent.isCancelled()) {
        }
    }
}
