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

package net.tridentsdk.server.packets.play.in;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.base.Position;
import net.tridentsdk.concurrent.ScheduledRunnable;
import net.tridentsdk.meta.block.SignMeta;
import net.tridentsdk.registry.Registered;
import net.tridentsdk.server.netty.ClientConnection;
import net.tridentsdk.server.netty.Codec;
import net.tridentsdk.server.netty.packet.InPacket;
import net.tridentsdk.server.netty.packet.Packet;
import net.tridentsdk.server.player.PlayerConnection;
import net.tridentsdk.server.player.TridentPlayer;
import net.tridentsdk.util.TridentLogger;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Packet is sent when a player wishes to update a sign
 */
public class PacketPlayInUpdateSign extends InPacket {
    /**
     * Contents of the sign, represented in JSON
     */
    protected final String[] jsonContents = new String[4];
    /**
     * Location of the sign
     */
    protected Position signLocation;

    @Override
    public int id() {
        return 0x2A;
    }

    @Override
    public Packet decode(ByteBuf buf) {
        long encoded = buf.readLong();
        double x = (double) (encoded >> 38);
        double y = (double) (encoded << 26 >> 52);
        double z = (double) (encoded << 38 >> 38);

        this.signLocation = Position.create(null, x, y, z);

        for (int i = 0; i < 4; i++) {
            this.jsonContents[i] = Codec.readString(buf);
        }
        return this;
    }

    public Position signLocation() {
        return this.signLocation;
    }

    public String[] jsonContents() {
        return this.jsonContents;
    }

    @Override
    public void handleReceived(ClientConnection connection) {
        TridentPlayer player = ((PlayerConnection) connection).player();
        signLocation.setWorld(player.world());

        Registered.tasks().asyncRepeat(null, new ScheduledRunnable() {
            private final AtomicInteger integer = new AtomicInteger();

            @Override
            public void run() {
                SignMeta meta = signLocation.block().obtainMeta(SignMeta.class);
                System.out.println("Meta at " + signLocation + " is " + signLocation.block().substance());
                integer.incrementAndGet();
                if (meta != null) {
                    for (int i = 0; i < 4; i++) {
                        meta.setTextAt(i, jsonContents[i]);
                    }
                    cancel();
                } else {
                    if (integer.get() > 10) {
                        TridentLogger.get().warn("Could not find sign at " + signLocation);
                        cancel();
                    }
                }
            }
        }, 1L, 1L);
    }
}
