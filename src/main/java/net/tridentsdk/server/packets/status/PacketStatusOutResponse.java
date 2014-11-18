/*
 *     Trident - A Multithreaded Server Alternative
 *     Copyright (C) 2014, The TridentSDK Team
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.tridentsdk.server.packets.status;

import com.google.gson.GsonBuilder;
import io.netty.buffer.ByteBuf;
import net.tridentsdk.server.netty.Codec;
import net.tridentsdk.server.netty.packet.OutPacket;

/**
 * Status response to PacketStatusInRequest
 *
 * @author The TridentSDK Team
 * @see net.tridentsdk.server.packets.status.PacketStatusInRequest
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
             * The slots of the server
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
