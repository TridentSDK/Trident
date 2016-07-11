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

package net.tridentsdk.server.netty.protocol;

import net.tridentsdk.docs.AccessNoDoc;
import net.tridentsdk.server.packets.play.in.*;
import net.tridentsdk.server.packets.play.out.*;

@AccessNoDoc
class Play extends ProtocolHandler {

    Play() {
        super.inPackets.put(0x00, PacketPlayInTeleportConfirm.class);
        super.inPackets.put(0x0B, PacketPlayInKeepAlive.class);
        super.inPackets.put(0x02, PacketPlayInChat.class);
        super.inPackets.put(0x0A, PacketPlayInEntityInteract.class);
        super.inPackets.put(0x0F, PacketPlayInPlayerFall.class);
        super.inPackets.put(0x0C, PacketPlayInPlayerMove.class);
        super.inPackets.put(0x0E, PacketPlayInPlayerLook.class);
        super.inPackets.put(0x0D, PacketPlayInPlayerCompleteMove.class);
        super.inPackets.put(0x13, PacketPlayInPlayerDig.class);
        super.inPackets.put(0x1C, PacketPlayInBlockPlace.class);
        super.inPackets.put(0x17, PacketPlayInPlayerHeldItemChange.class);
        super.inPackets.put(0x1A, PacketPlayInAnimation.class);
        super.inPackets.put(0x14, PacketPlayInEntityAction.class);
        super.inPackets.put(0x15, PacketPlayInSteerVehicle.class);
        super.inPackets.put(0x08, PacketPlayInPlayerCloseWindow.class);
        super.inPackets.put(0x07, PacketPlayInPlayerClickWindow.class);
        super.inPackets.put(0x05, PacketPlayInPlayerConfirmTransaction.class);
        super.inPackets.put(0x18, PacketPlayInPlayerCreativeAction.class);
        super.inPackets.put(0x06, PacketPlayInPlayerEnchant.class);
        super.inPackets.put(0x2A, PacketPlayInUpdateSign.class);
        super.inPackets.put(0x12, PacketPlayInPlayerAbilities.class);
        super.inPackets.put(0x01, PacketPlayInTabComplete.class);
        super.inPackets.put(0x04, PacketPlayInClientSettings.class);
        super.inPackets.put(0x03, PacketPlayInClientStatus.class);
        super.inPackets.put(0x09, PacketPlayInPluginMessage.class);
        super.inPackets.put(0x1B, PacketPlayInPlayerSpectate.class);
        super.inPackets.put(0x16, PacketPlayInPackStatus.class);

        super.outPackets.put(0x1F, PacketPlayOutKeepAlive.class);
        super.outPackets.put(0x23, PacketPlayOutJoinGame.class);
        super.outPackets.put(0x0F, PacketPlayOutChat.class);
        super.outPackets.put(0x44, PacketPlayOutTimeUpdate.class);
        super.outPackets.put(0x04, PacketPlayOutEntityEquipment.class);
        super.outPackets.put(0x43, PacketPlayOutSpawnPosition.class);
        super.outPackets.put(0x06, PacketPlayOutUpdateHealth.class);
        super.outPackets.put(0x07, PacketPlayOutPlayerRespawn.class);
        super.outPackets.put(0x08, PacketPlayOutPlayerCompleteMove.class);
        super.outPackets.put(0x09, PacketPlayOutPlayerHeldItemChange.class);
        super.outPackets.put(0x0A, PacketPlayOutUseBed.class);
        super.outPackets.put(0x0B, PacketPlayOutAnimation.class);
        super.outPackets.put(0x05, PacketPlayOutSpawnPlayer.class);
        super.outPackets.put(0x0D, PacketPlayOutCollectItem.class);
        super.outPackets.put(0x0E, PacketPlayOutSpawnObject.class);
        super.outPackets.put(0x0F, PacketPlayOutSpawnMob.class);
        super.outPackets.put(0x10, PacketPlayOutSpawnPainting.class);
        super.outPackets.put(0x11, PacketPlayOutSpawnExperienceOrb.class);
        super.outPackets.put(0x3B, PacketPlayOutEntityVelocity.class);
        super.outPackets.put(0x13, PacketPlayOutDestroyEntities.class);
        super.outPackets.put(0x14, PacketPlayOutEntityTick.class);
        super.outPackets.put(0x15, PacketPlayOutEntityRelativeMove.class);
        super.outPackets.put(0x16, PacketPlayOutEntityLook.class);
        super.outPackets.put(0x17, PacketPlayOutEntityCompleteMove.class);
        super.outPackets.put(0x18, PacketPlayOutEntityTeleport.class);
        super.outPackets.put(0x19, PacketPlayOutEntityHeadLook.class);
        super.outPackets.put(0x1A, PacketPlayOutEntityStatus.class);
        super.outPackets.put(0x1B, PacketPlayOutAttachEntity.class);
        super.outPackets.put(0x1C, PacketPlayOutEntityMetadata.class);
        super.outPackets.put(0x1D, PacketPlayOutEntityEffect.class);
        super.outPackets.put(0x1E, PacketPlayOutRemoveEffect.class);
        super.outPackets.put(0x1F, PacketPlayOutSetExperience.class);
        super.outPackets.put(0x4B, PacketPlayOutEntityProperties.class);
        super.outPackets.put(0x20, PacketPlayOutChunkData.class);
        super.outPackets.put(0x22, PacketPlayOutMultiBlockChange.class);
        super.outPackets.put(0x23, PacketPlayOutBlockChange.class);
        super.outPackets.put(0x24, PacketPlayOutBlockAction.class);
        super.outPackets.put(0x25, PacketPlayOutBlockBreakAnimation.class);
        super.outPackets.put(0x27, PacketPlayOutExplosion.class);
        super.outPackets.put(0x28, PacketPlayOutEffect.class);
        super.outPackets.put(0x29, PacketPlayOutSoundEffect.class);
        super.outPackets.put(0x2A, PacketPlayOutParticle.class);
        super.outPackets.put(0x1E, PacketPlayOutGameStateChange.class);
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
        super.outPackets.put(0x07, PacketPlayOutStatistics.class);
        super.outPackets.put(0x2D, PacketPlayOutPlayerListItem.class);
        super.outPackets.put(0x2B, PacketPlayOutPlayerAbilities.class);
        super.outPackets.put(0x3A, PacketPlayOutTabComplete.class);
        super.outPackets.put(0x3B, PacketPlayOutScoreboardObjective.class);
        super.outPackets.put(0x3C, PacketPlayOutUpdateScore.class);
        super.outPackets.put(0x3D, PacketPlayOutDisplayScoreboard.class);
        super.outPackets.put(0x3E, PacketPlayOutTeams.class);
        super.outPackets.put(0x18, PacketPlayOutPluginMessage.class);
        super.outPackets.put(0x40, PacketPlayOutDisconnect.class);
        super.outPackets.put(0x0D, PacketPlayOutServerDifficulty.class);
        super.outPackets.put(0x42, PacketPlayOutCombatEvent.class);
        super.outPackets.put(0x43, PacketPlayOutCamera.class);
        super.outPackets.put(0x44, PacketPlayOutWorldBorder.class);
        super.outPackets.put(0x45, PacketPlayOutTitle.class);
        super.outPackets.put(0x46, PacketPlayOutSetCompression.class);
        super.outPackets.put(0x47, PacketPlayOutPlayerListHeaderFooter.class);
        super.outPackets.put(0x48, PacketPlayOutResourcePackSend.class);
        super.outPackets.put(0x49, PacketPlayOutUpdateEntityNBT.class);
    }
}