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
package net.tridentsdk.server.packet.login;

import net.tridentsdk.server.net.NetPayload;
import net.tridentsdk.server.packet.PacketOut;

/**
 * Packet sent by the server after {@link LoginInStart} to
 * attempt to request the client to use packet encryption.
 */
public class LoginOutEncryptionRequest extends PacketOut {
    /**
     * The encoded public key
     */
    private final byte[] publicKey;
    /**
     * The token
     */
    private final byte[] token;

    public LoginOutEncryptionRequest(byte[] publicKey, byte[] token) {
        super(LoginOutEncryptionRequest.class);
        this.publicKey = publicKey;
        this.token = token;
    }

    @Override
    public void write(NetPayload payload) {
        payload.writeVInt(0); // empty string

        payload.writeVInt(this.publicKey.length);
        payload.writeBytes(this.publicKey);
        payload.writeVInt(this.token.length);
        payload.writeBytes(this.token);
    }
}