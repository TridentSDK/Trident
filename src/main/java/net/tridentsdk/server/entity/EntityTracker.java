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
package net.tridentsdk.server.entity;

import net.tridentsdk.Coordinates;
import net.tridentsdk.Trident;
import net.tridentsdk.docs.InternalUseOnly;
import net.tridentsdk.entity.Entity;
import net.tridentsdk.entity.living.Player;
import net.tridentsdk.event.player.PlayerMoveEvent;
import net.tridentsdk.server.packets.play.out.PacketPlayOutEntityCompleteMove;
import net.tridentsdk.server.packets.play.out.PacketPlayOutSpawnMob;
import net.tridentsdk.server.player.TridentPlayer;
import net.tridentsdk.util.Vector;

/**
 * Adds tracking capability to the server to watch entity properties such as movement and updates
 *
 * @author The TridentSDK Team
 */
@InternalUseOnly
public class EntityTracker {
    public void track(Entity entity, byte... meta) {
        if (entity instanceof TridentPlayer)
            return;
        PacketPlayOutSpawnMob packet = new PacketPlayOutSpawnMob();
        packet.set("entityId", entity.getId())
                .set("type", entity.getType())
                .set("entity", entity)
                .set("metadata", meta == null ? new byte[] { (byte) ((1 << 5 | 1 & 0x1F) & 0xFF), (short) 10 } : meta);
        // TODO
        TridentPlayer.sendAll(packet);
        entity.getWorld().getEntities().add(entity);
    }

    public void trackMovement(Entity entity, Coordinates from, Coordinates to) {
        // TODO right order?
        Vector diff = from.toVector().subtract(to.toVector());

        if (entity instanceof Player) {
            PlayerMoveEvent event = new PlayerMoveEvent((Player) entity, from, to);
            Trident.getEventManager().call(event);
            if (!event.isIgnored())
                sendMove(entity, to, diff);

            return;
        }

        sendMove(entity, to, diff);
    }

    private void sendMove(Entity entity, Coordinates to, Vector diff) {
        PacketPlayOutEntityCompleteMove move = new PacketPlayOutEntityCompleteMove();
        move.set("entityId", entity.getId())
                .set("difference", diff)
                .set("yaw", to.getYaw())
                .set("pitch", to.getPitch())
                .set("flags", null);
        TridentPlayer.sendAll(move);
    }
}
