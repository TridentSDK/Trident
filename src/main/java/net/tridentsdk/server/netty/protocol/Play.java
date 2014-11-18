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
package net.tridentsdk.server.netty.protocol;

import net.tridentsdk.api.docs.AccessNoDoc;
import net.tridentsdk.server.packets.play.in.*;
import net.tridentsdk.server.packets.play.out.*;

@AccessNoDoc
class Play extends PacketManager {

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