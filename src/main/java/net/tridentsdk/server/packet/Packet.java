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

import javax.annotation.concurrent.Immutable;

/**
 * This class represents an abstraction of a data packet
 * that is used by the Minecraft protocol to communicate
 * units of information between the client(s) and the
 * server.
 */
@Immutable
public class Packet {
    /**
     * The direction which a packet is sent towards.
     */
    public enum Bound {
        /**
         * Client-bound, out packets.
         */
        CLIENT,
        /**
         * Server-bound, in packets.
         */
        SERVER;

        /**
         * Obtain the bound of the packet represented by
         * the
         * given class.
         *
         * @param cls the class to determine the bound
         * @return the bound of the packet
         */
        public Bound of(Class<? extends Packet> cls) {
            if (cls.getSuperclass() == PacketIn.class) {
                return SERVER;
            } else {
                return CLIENT;
            }
        }
    }

    /**
     * The packet ID
     */
    private final int id;

    /**
     * The constructor which polls the packet registry in
     * order to setup the initializing fields.
     *
     * @param cls the class of the packet to be registered
     */
    public Packet(Class<? extends Packet> cls) {
        int info = PacketRegistry.packetInfo(cls);
        this.id = PacketRegistry.idOf(info);
    }

    /**
     * Obtains the packet ID which identifies the packet
     * once it has been sent to or from the client.
     *
     * @return the packet ID
     */
    public int id() {
        return this.id;
    }
}