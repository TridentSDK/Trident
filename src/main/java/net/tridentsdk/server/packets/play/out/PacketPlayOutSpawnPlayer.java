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
package net.tridentsdk.server.packets.play.out;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.Coordinates;
import net.tridentsdk.entity.living.Player;
import net.tridentsdk.server.netty.Codec;
import net.tridentsdk.server.netty.packet.OutPacket;

import java.util.UUID;

public class PacketPlayOutSpawnPlayer extends OutPacket {
    protected int entityId;
    protected Player player;
    // TODO: Mojang slot stuff
    // TODO: entity metadata

    @Override
    public int getId() {
        return 0x0C;
    }

    public int getEntityId() {
        return this.entityId;
    }

    public Player getPlayer() {
        return this.player;
    }

    @Override
    public void encode(ByteBuf buf) {
        Coordinates loc = this.player.getLocation();
        UUID id = this.player.getUniqueId();

        Codec.writeVarInt32(buf, this.entityId);

        buf.writeLong(id.getMostSignificantBits());
        buf.writeLong(id.getLeastSignificantBits());

        buf.writeInt((int) loc.getX() * 32);
        buf.writeInt((int) loc.getY() * 32);
        buf.writeInt((int) loc.getZ() * 32);

        buf.writeByte((int) (byte) loc.getYaw());
        buf.writeByte((int) (byte) loc.getPitch());
    }
}
