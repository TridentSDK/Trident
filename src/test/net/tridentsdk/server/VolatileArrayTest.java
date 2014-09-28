/*
 * Copyright (c) 2014, The TridentSDK Team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     1. Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *     2. Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *     3. Neither the name of the The TridentSDK Team nor the
 *        names of its contributors may be used to endorse or promote products
 *        derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL The TridentSDK Team BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.tridentsdk.server;

import io.netty.channel.Channel;
import net.tridentsdk.server.netty.protocol.Protocol;

import java.net.InetSocketAddress;
import java.security.SecureRandom;

public final class VolatileArrayTest {
    private VolatileArrayTest() {
    }

    public static void main(String... args) {
        /*
        final PropertyBuilder builder = new PropertyBuilder(100);
        for (int i = 0; i < 100; i++) {
            final int i1 = i;
            new Thread(new Runnable() {
                @Override public void run() {
                    builder.addModifier(i1, String.valueOf(i1));
                }
            }).start();
        }

        for (String string : builder.getModifiers()) {
            System.out.println(string);
        } THIS WORKS
        */

        ClientConnection connection = new ClientConnection(new CTXProper().channel());
        connection.generateToken();
        System.out.print(connection.getVerificationToken().length);
        // THIS WORKS TOO!
    }

    // Shortened version
    public static class ClientConnection {
        protected final SecureRandom SR = new SecureRandom();

        /* Network fields */
        protected InetSocketAddress address;
        protected Channel channel;

        /* Encryption and client data fields */
        protected volatile Protocol.ClientStage stage;
        protected volatile boolean encryptionEnabled;
        protected volatile byte[] verificationToken; // DO NOT WRITE INDIVIDUAL ELEMENTS TO IT. Consult AgentTroll

        /**
         * Creates a new connection handler for the joining channel stream
         */
        protected ClientConnection(Channel channel) {
            this.address = (InetSocketAddress) channel.remoteAddress();
            this.channel = channel;
            this.encryptionEnabled = false;
            this.stage = Protocol.ClientStage.HANDSHAKE;
        }

        public void generateToken() {
            this.verificationToken = new byte[4];
            this.SR.nextBytes(this.verificationToken);
        }

        public InetSocketAddress getAddress() {
            return this.address;
        }

        public byte[] getVerificationToken() {
            return this.verificationToken;
        }
    }
}
