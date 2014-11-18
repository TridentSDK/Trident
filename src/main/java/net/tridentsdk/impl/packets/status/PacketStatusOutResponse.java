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
package net.tridentsdk.impl.packets.status;

import com.google.gson.GsonBuilder;
import io.netty.buffer.ByteBuf;
import net.tridentsdk.impl.netty.Codec;
import net.tridentsdk.impl.netty.packet.OutPacket;

/**
 * Status response to PacketStatusInRequest
 *
 * @author The TridentSDK Team
 * @see PacketStatusInRequest
 */
public class PacketStatusOutResponse extends OutPacket {
    /**
     * The actual response, represented in JSON in the protocol
     */
    Response response;

    public PacketStatusOutResponse() {
        this.response = new Response();
    }

    @Override
    public int getId() {
        return 0x00;
    }

    public Response getResponse() {
        return this.response;
    }

    @Override
    public void encode(ByteBuf buf) {
        String json = new GsonBuilder().create().toJson(this.response);
        Codec.writeString(buf, json);
    }

    public static class Response {
        /**
         * Version information
         */
        Version version = new Version();
        /**
         * Information regarding players
         */
        final Players players = new Players();
        /**
         * Description is the MOTD
         */
        final Description description = new Description();

        public static class Version {
            /**
             * Name of the version TODO make configurable
             */
            String name = "1.8";
            /**
             * Protocol version, 47 for 1.8
             */
            int protocol = 47;
        }

        public static class Players {
            /**
             * The slots of the impl
             */
            int max = 10;
            /**
             * Amount of players online
             */
            int online = 5;
        }

        public static class Description {
            /**
             * MOTD
             */
            String text = "default blah blah this is never going to show";
        }
    }
}
