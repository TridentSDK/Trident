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
package net.tridentsdk.server.packet;

import net.tridentsdk.server.net.NetClient;
import net.tridentsdk.server.net.NetPayload;

/**
 * Represents a server-bound packet that a Minecraft client
 * sends to the server.
 */
public abstract class PacketIn extends Packet {
    /**
     * The constructor which polls the packet registry in
     * order to setup the initializing fields.
     *
     * @param cls the class of the packet to be registered
     */
    public PacketIn(Class<? extends Packet> cls) {
        super(cls);
    }

    /**
     * Reads the payload data that was sent by the injected
     * sender.
     *
     * @param payload the payload of the packet
     * @param sender the sender
     */
    public abstract void read(NetPayload payload, NetClient sender);
}