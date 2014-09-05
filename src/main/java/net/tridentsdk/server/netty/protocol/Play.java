/*
 * Copyright (C) 2014 The TridentSDK Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.tridentsdk.server.netty.protocol;

import net.tridentsdk.api.docs.AccessNoDoc;
import net.tridentsdk.packets.play.in.*;

@AccessNoDoc class Play extends PacketManager {

    Play() {
        this.inPackets.put(0x00, PacketPlayInKeepAlive.class);
        this.inPackets.put(0x01, PacketPlayInChat.class);
        this.inPackets.put(0x02, PacketPlayInEntityInteract.class);
        this.inPackets.put(0x03, PacketPlayInPlayerFall.class);
        this.inPackets.put(0x04, PacketPlayInPlayerMove.class);
        this.inPackets.put(0x05, PacketPlayInPlayerLook.class);
        this.inPackets.put(0x06, PacketPlayInPlayerCompleteMove.class);
        this.inPackets.put(0x07, PacketPlayInPlayerDig.class);
        this.inPackets.put(0x08, PacketPlayInBlockPlace.class);
        this.inPackets.put(0x09, PacketPlayInPlayerHeldItemChange.class);
        this.inPackets.put(0x0A, PacketPlayInAnimation.class); // I don't even
        this.inPackets.put(0x0B, PacketPlayInEntityAction.class);
        this.inPackets.put(0x0C, PacketPlayInSteerVehicle.class);
        this.inPackets.put(0x0D, PacketPlayInPlayerCloseWindow.class);
        this.inPackets.put(0x0E, PacketPlayInPlayerClickWindow.class);
        this.inPackets.put(0x0F, PacketPlayInPlayerConfirmTransaction.class);
        this.inPackets.put(0x10, PacketPlayInPlayerCreativeAction.class);
        this.inPackets.put(0x11, PacketPlayInPlayerEnchant.class);
        this.inPackets.put(0x12, PacketPlayInUpdateSign.class);
        this.inPackets.put(0x13, PacketPlayInPlayerAbilities.class);
        this.inPackets.put(0x14, PacketPlayInTabComplete.class);
        this.inPackets.put(0x15, PacketPlayInClientSettings.class);
        this.inPackets.put(0x16, PacketPlayInClientStatus.class);
        this.inPackets.put(0x17, PacketPlayInPluginMessage.class);
        this.inPackets.put(0x18, PacketPlayInPlayerSpectate.class);
        this.inPackets.put(0x19, PacketPlayInPackStatus.class);
    }
}