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
import net.tridentsdk.packets.play.out.*;

@AccessNoDoc class Play extends PacketManager {

    Play() {
        super.inPackets.put(0x00, PacketPlayInKeepAlive.class);
        super.inPackets.put(0x01, PacketPlayInChat.class);
        super.inPackets.put(0x02, PacketPlayInEntityInteract.class);
        super.inPackets.put(0x03, PacketPlayInPlayerFall.class);
        super.inPackets.put(0x04, PacketPlayInPlayerMove.class);
        super.inPackets.put(0x05, PacketPlayInPlayerLook.class);
        super.inPackets.put(0x06, PacketPlayInPlayerCompleteMove.class);
        //TODO: Work on PlayeInDig Packet
        //super.inPackets.put(0x07, PacketPlayInPlayerDig.class);
        super.inPackets.put(0x08, PacketPlayInBlockPlace.class);
        super.inPackets.put(0x09, PacketPlayInPlayerHeldItemChange.class);
        super.inPackets.put(0x0A, PacketPlayInAnimation.class); // I don't even
        super.inPackets.put(0x0B, PacketPlayInEntityAction.class);
        super.inPackets.put(0x0C, PacketPlayInSteerVehicle.class);
        super.inPackets.put(0x0D, PacketPlayInPlayerCloseWindow.class);
        super.inPackets.put(0x0E, PacketPlayInPlayerClickWindow.class);
        super.inPackets.put(0x0F, PacketPlayInPlayerConfirmTransaction.class);
        super.inPackets.put(0x10, PacketPlayInPlayerCreativeAction.class);
        super.inPackets.put(0x11, PacketPlayInPlayerEnchant.class);
        super.inPackets.put(0x12, PacketPlayInUpdateSign.class);
        super.inPackets.put(0x13, PacketPlayInPlayerAbilities.class);
        super.inPackets.put(0x14, PacketPlayInTabComplete.class);
        super.inPackets.put(0x15, PacketPlayInClientSettings.class);
        super.inPackets.put(0x16, PacketPlayInClientStatus.class);
        super.inPackets.put(0x17, PacketPlayInPluginMessage.class);
        super.inPackets.put(0x18, PacketPlayInPlayerSpectate.class);
        super.inPackets.put(0x19, PacketPlayInPackStatus.class);

        super.outPackets.put(0x00, PacketPlayOutKeepAlive.class);
        super.outPackets.put(0x01, PacketPlayOutJoinGame.class);
        super.outPackets.put(0x02, PacketPlayOutChatMessage.class);
        super.outPackets.put(0x03, PacketPlayOutTimeUpdate.class);
        super.outPackets.put(0x04, PacketPlayOutEntityEquipment.class);
        super.outPackets.put(0x05, PacketPlayOutSpawnPosition.class);
        super.outPackets.put(0x06, PacketPlayOutUpdateHealth.class);
        super.outPackets.put(0x07, PacketPlayOutPlayerRespawn.class);
        super.outPackets.put(0x08, PacketPlayOutPlayerCompleteMove.class);
        super.outPackets.put(0x09, PacketPlayOutPlayerHeldItemChange.class);
        super.outPackets.put(0x0A, PacketPlayOutUseBed.class);
        super.outPackets.put(0x0B, PacketPlayOutAnimation.class);
        super.outPackets.put(0x0C, PacketPlayOutSpawnPlayer.class);
        super.outPackets.put(0x0D, PacketPlayOutSpawnObject.class);
        super.outPackets.put(0x0F, PacketPlayOutSpawnMob.class);
        super.outPackets.put(0x10, PacketPlayOutSpawnPainting.class);
        super.outPackets.put(0x11, PacketPlayOutSpawnExperienceOrb.class);
        super.outPackets.put(0x12, PacketPlayOutEntityVelocity.class);
        super.outPackets.put(0x13, PacketPlayOutDestroyEntities.class);
        super.outPackets.put(0x14, PacketPlayOutEntityTick.class);
        super.outPackets.put(0x15, PacketPlayOutEntityLook.class);
        super.outPackets.put(0x16, PacketPlayOutEntityLook.class);
        super.outPackets.put(0x17, PacketPlayOutEntityCompleteMove.class);
        super.outPackets.put(0x18, PacketPlayOutEntityTeleport.class);
        super.outPackets.put(0x19, PacketPlayOutEntityHeadLook.class);
        super.outPackets.put(0x1A, PacketPlayOutEntityStatus.class);
        super.outPackets.put(0x1D, PacketPlayOutEntityEffect.class);
        super.outPackets.put(0x1E, PacketPlayOutRemoveEffect.class);
        super.outPackets.put(0x1F, PacketPlayOutSetExperience.class);
        super.outPackets.put(0x20, PacketPlayOutEntityProperties.class);
        super.outPackets.put(0x21, PacketPlayOutChunkData.class);
        super.outPackets.put(0x22, PacketPlayOutMultiBlockChange.class);
        super.outPackets.put(0x23, PacketPlayOutBlockChange.class);
        super.outPackets.put(0x24, PacketPlayOutBlockAction.class);
        super.outPackets.put(0x25, PacketPlayOutBlockBreakAnimation.class);
        super.outPackets.put(0x26, PacketPlayOutMapChunkBulk.class);
        super.outPackets.put(0x27, PacketPlayOutExplosion.class);
        super.outPackets.put(0x28, PacketPlayOutEntityEffect.class);
        super.outPackets.put(0x29, PacketPlayOutSoundEffect.class);
        super.outPackets.put(0x2A, PacketPlayOutParticle.class);
        super.outPackets.put(0x2B, PacketPlayOutGameStateChange.class);
        super.outPackets.put(0x2C, PacketPlayOutSpawnGlobalEntity.class);
        super.outPackets.put(0x2D, PacketPlayOutOpenWindow.class);
        super.outPackets.put(0x2E, PacketPlayOutCloseWindow.class);
        super.outPackets.put(0x2F, PacketPlayOutSetSlot.class);
        super.outPackets.put(0x30, PacketPlayOutWindowItems.class);
        super.outPackets.put(0x31, PacketPlayOutWindowProperty.class);
        super.outPackets.put(0x32, PacketPlayOutConfirmTransaction.class);
        super.outPackets.put(0x33, PacketPlayOutUpdateSign.class);
        super.outPackets.put(0x34, PacketPlayOutMaps.class);
        super.outPackets.put(0x35, PacketPlayOutUpdateBlockEntity.class);
        super.outPackets.put(0x36, PacketPlayOutSignEditorOpen.class);
        super.outPackets.put(0x37, PacketPlayOutStatistics.class);
        super.outPackets.put(0x38, PacketPlayOutPlayerListItem.class);
        super.outPackets.put(0x39, PacketPlayOutPlayerAbilities.class);
        super.outPackets.put(0x3A, PacketPlayOutTabComplete.class);
        super.outPackets.put(0x3B, PacketPlayOutScoreboardObjective.class);
        super.outPackets.put(0x3C, PacketPlayOutUpdateScore.class);
        super.outPackets.put(0x3D, PacketPlayOutDisplayScoreboard.class);
        super.outPackets.put(0x3E, PacketPlayOutTeams.class);
        super.outPackets.put(0x3F, PacketPlayOutPluginMessage.class);
        super.outPackets.put(0x40, PacketPlayOutDisconnect.class);
        super.outPackets.put(0x41, PacketPlayOutServerDifficulty.class);
        super.outPackets.put(0x42, PacketPlayOutCombatEvent.class);
        super.outPackets.put(0x43, PacketPlayOutCamera.class);
        super.outPackets.put(0x44, PacketPlayOutWorldBorder.class);
        super.outPackets.put(0x45, PacketPlayOutTitle.class);
        super.outPackets.put(0x46, PacketPlayOutSetCompression.class);
        super.outPackets.put(0x47, PacketPlayOutPlayerListUpdate.class);
        super.outPackets.put(0x48, PacketPlayOutResourcePackSend.class);
        super.outPackets.put(0x49, PacketPlayOutUpdateEntityNBT.class);
    }
}