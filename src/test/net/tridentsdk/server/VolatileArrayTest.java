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
                @Override
                public void run() {
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
        protected final InetSocketAddress address;
        protected final Channel channel;

        /* Encryption and client data fields */
        protected final Protocol.ClientStage stage;
        protected final boolean encryptionEnabled;
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
