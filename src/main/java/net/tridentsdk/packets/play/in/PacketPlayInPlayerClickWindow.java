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
import net.tridentsdk.api.event.player.PlayerClickItemEvent;
import net.tridentsdk.api.window.Window;
import net.tridentsdk.data.Slot;
import net.tridentsdk.server.TridentServer;
import net.tridentsdk.server.netty.ClientConnection;
import net.tridentsdk.server.netty.packet.InPacket;
import net.tridentsdk.server.netty.packet.Packet;

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
        PlayerClickItemEvent clickEvent = new PlayerClickItemEvent(window, this.clickedSlot, this.actionNumber);

        TridentServer.getInstance().getEventManager().call(clickEvent);

        if (clickEvent.isCancelled()) {
        }
    }
}
