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

import com.google.common.collect.Maps;
import io.netty.buffer.ByteBuf;
import net.tridentsdk.server.net.NetClient;
import net.tridentsdk.server.packet.PacketIn;

import javax.annotation.concurrent.Immutable;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static net.tridentsdk.server.net.NetData.rvint;

/**
 * Sent by the client in order to enusre that the
 * connection
 * remains active.
 */
@Immutable
public final class PlayInKeepAlive extends PacketIn {
    /**
     * The ID number source
     */
    private static final AtomicInteger ID_COUNTER = new AtomicInteger();

    /**
     * The keep alive time cache
     */
    private static final Map<NetClient, Integer> TICK_IDS =
            Maps.newConcurrentMap();

    /**
     * Obtains the next keep alive ID for the given net
     * client
     *
     * @param client the client
     * @return the next teleport ID
     */
    public static int query(NetClient client) {
        int id = ID_COUNTER.incrementAndGet();
        if (id > 1_000_000 && !TICK_IDS.containsValue(10_000)) {
            ID_COUNTER.set(0);
        }

        TICK_IDS.put(client, id);
        return id;
    }

    public PlayInKeepAlive() {
        super(PlayInKeepAlive.class);
    }

    @Override
    public void read(ByteBuf buf, NetClient client) {
        int id = rvint(buf);
        Integer localId = TICK_IDS.get(client);

        if (localId != null && id != localId) {
            client.disconnect("Keep alive ID mismatch, actual:" + localId + " rcvd:" + id);
        }

        if ((System.nanoTime() - client.lastKeepAlive()) > NetClient.KEEP_ALIVE_KICK_NANOS) {
            client.disconnect("Timed out");
        }
    }
}