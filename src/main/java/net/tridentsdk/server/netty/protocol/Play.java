/*
 * Copyright (c) 2014, The TridentSDK Team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     1. Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *     2. Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *     3. Neither the name of the The TridentSDK Team nor the
 *        names of its contributors may be used to endorse or promote products
 *        derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL The TridentSDK Team BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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