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

@AccessNoDoc
class Play extends PacketManager {

    Play() {
        inPackets.put(0x00, PacketPlayInKeepAlive.class);
        inPackets.put(0x01, PacketPlayInChat.class);
        inPackets.put(0x02, PacketPlayInEntityInteract.class);
        inPackets.put(0x03, PacketPlayInPlayerFall.class);
        inPackets.put(0x04, PacketPlayInPlayerMove.class);
        inPackets.put(0x05, PacketPlayInPlayerLook.class);
        inPackets.put(0x06, PacketPlayInPlayerCompleteMove.class);
        inPackets.put(0x07, PacketPlayInPlayerDig.class);
        inPackets.put(0x08, PacketPlayInBlockPlace.class);
        inPackets.put(0x09, PacketPlayInPlayerHeldItemChange.class);
        inPackets.put(0x0A, PacketPlayInAnimation.class); // I don't even
        inPackets.put(0x0B, PacketPlayInEntityAction.class);
        inPackets.put(0x0C, PacketPlayInSteerVehicle.class);
        inPackets.put(0x0D, PacketPlayInPlayerCloseWindow.class);
        inPackets.put(0x0E, PacketPlayInPlayerClickWindow.class);
        inPackets.put(0x0F, PacketPlayInPlayerConfirmTransaction.class);
        inPackets.put(0x10, PacketPlayInPlayerCAction.class);
        inPackets.put(0x11, PacketPlayInPlayerEnchant.class);
        inPackets.put(0x12, PacketPlayInUpdateSign.class);
        inPackets.put(0x13, PacketPlayInPlayerAbilities.class);
        inPackets.put(0x14, PacketPlayInTabComplete.class);
        inPackets.put(0x15, PacketPlayInClientSettings.class);
        inPackets.put(0x16, PacketPlayInClientStatus.class);
        inPackets.put(0x17, PacketPlayInPluginMessage.class);
        inPackets.put(0x18, PacketPlayInPlayerSpectate.class);
        inPackets.put(0x19, PacketPlayInPackStatus.class);
    }
}