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

package net.tridentsdk.server.packets.login;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.server.netty.ClientConnection;
import net.tridentsdk.server.netty.Codec;
import net.tridentsdk.server.netty.packet.OutPacket;
import net.tridentsdk.server.netty.packet.PacketDirection;

/**
 * Indicates a successful login
 *
 * @author The TridentSDK Team
 */
public class PacketLoginOutSuccess extends OutPacket {
    /**
     * UUID of the client, represented as a String and contains dashes
     */
    protected String uuid;
    /**
     * Username of the client
     */
    protected String username;
    /**
     * Connection of the client, currently not used
     */
    protected ClientConnection connection;

    @Override
    public int id() {
        return 0x02;
    }

    @Override
    public PacketDirection direction() {
        return PacketDirection.OUT;
    }

    public ClientConnection connection() {
        return this.connection;
    }

    public String uniqueId() {
        return this.uuid;
    }

    public String username() {
        return this.username;
    }

    @Override
    public void encode(ByteBuf buf) {
        Codec.writeString(buf, this.uuid);
        Codec.writeString(buf, this.username);
    }
}
