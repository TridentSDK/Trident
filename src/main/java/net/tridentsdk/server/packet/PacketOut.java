/*
 * Trident - A Multithreaded Server Alternative
 * Copyright 2017 The TridentSDK Team
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
package net.tridentsdk.server.packet;

import io.netty.buffer.ByteBuf;

import javax.annotation.concurrent.Immutable;

/**
 * Represents a client-bound packet that is sent by the
 * server.
 */
@Immutable
public abstract class PacketOut extends Packet {
    /**
     * The constructor which polls the packet registry in
     * order to setup the initializing fields.
     *
     * @param cls the class of the packet to be registered
     */
    public PacketOut(Class<? extends Packet> cls) {
        super(cls);
    }

    /**
     * Writes the buf of a client-bound packet.
     *
     * <p>Packet headers and compression are automatically
     * handled when the packet is encoded to the pipeline.
     * </p>
     *
     * @param buf the buf to be written
     */
    public abstract void write(ByteBuf buf);
}