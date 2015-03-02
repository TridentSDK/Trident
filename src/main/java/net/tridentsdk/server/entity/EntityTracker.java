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

import net.tridentsdk.Handler;
import net.tridentsdk.Position;
import net.tridentsdk.docs.InternalUseOnly;
import net.tridentsdk.entity.Entity;
import net.tridentsdk.entity.living.Player;
import net.tridentsdk.event.player.PlayerMoveEvent;
import net.tridentsdk.server.packets.play.out.PacketPlayOutEntityCompleteMove;
import net.tridentsdk.server.packets.play.out.PacketPlayOutSpawnMob;
import net.tridentsdk.server.player.TridentPlayer;
import net.tridentsdk.server.world.TridentWorld;
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
        packet.set("entityId", entity.entityId())
                .set("type", entity.type())
                .set("entity", entity)
                .set("metadata", ((TridentEntity) entity).protocolMeta);
        // TODO
        TridentPlayer.sendAll(packet);
        ((TridentWorld) entity.world()).addEntity(entity);
    }

    public void trackMovement(Entity entity, Position from, Position to) {
        // TODO right order?
        Vector diff = from.asVector().subtract(to.asVector());

        if (entity instanceof Player) {
            PlayerMoveEvent event = new PlayerMoveEvent((Player) entity, from, to);
            Handler.forEvents().fire(event);
            if (!event.isIgnored())
                sendMove(entity, to, diff);

            return;
        }

        sendMove(entity, to, diff);
    }

    private void sendMove(Entity entity, Position to, Vector diff) {
        PacketPlayOutEntityCompleteMove move = new PacketPlayOutEntityCompleteMove();
        move.set("entityId", entity.entityId())
                .set("difference", diff)
                .set("yaw", to.yaw())
                .set("pitch", to.pitch())
                .set("flags", (byte) 0);
        TridentPlayer.sendAll(move);
    }
}
