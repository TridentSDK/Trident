/*
 * Trident - A Multithreaded Server Alternative
 * Copyright 2016 The TridentSDK Team
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
package net.tridentsdk.server.packet.play;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.command.logger.Logger;
import net.tridentsdk.server.net.NetClient;
import net.tridentsdk.server.packet.PacketIn;
import net.tridentsdk.util.Cache;

import javax.annotation.concurrent.ThreadSafe;
import java.util.concurrent.atomic.AtomicInteger;

import static net.tridentsdk.server.net.NetData.rvint;

/**
 * Teleportation confirm packet. Sent after packets such as
 * {@link PlayOutPosLook} to confirm the ID.
 */
@ThreadSafe
public final class PlayInTeleportConfirm extends PacketIn {
    /**
     * The ID number source
     */
    private static final AtomicInteger ID_COUNTER = new AtomicInteger();
    /**
     * The teleport ID cache
     */
    private static final Cache<NetClient, Integer> TELEPORT_ID =
            new Cache<>(NetClient.KEEP_ALIVE_KICK_NANOS / 1000000, (client, id) -> client.disconnect("No teleport response"));

    /**
     * Obtains the next teleport ID for the given net
     * client
     *
     * @param client the client
     * @return the next teleport ID
     */
    public static int query(NetClient client) {
        int id = ID_COUNTER.incrementAndGet();
        if (id > 1_000_000) {
            ID_COUNTER.set(0);
        }

        TELEPORT_ID.put(client, id);
        return id;
    }

    public PlayInTeleportConfirm() {
        super(PlayInTeleportConfirm.class);
    }

    @Override
    public void read(ByteBuf buf, NetClient client) {
        int id = rvint(buf);
        Integer localId = TELEPORT_ID.getIfPresent(client);

        // TODO Fix teleport confirmation
        if (localId != null && localId == id) {
            //client.player().resumeLogin();
        } else {
            Logger.get(PlayInTeleportConfirm.class).error("Teleport ID mismatch, actual:" + localId + " received:" + id);
            //client.disconnect("Teleport ID mismatch, actual:" + localId + " rcvd:" + id);
        }
    }
}