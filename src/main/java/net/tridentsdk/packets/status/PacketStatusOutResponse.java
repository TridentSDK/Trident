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

package net.tridentsdk.packets.status;

import com.google.gson.GsonBuilder;
import io.netty.buffer.ByteBuf;
import net.tridentsdk.server.netty.Codec;
import net.tridentsdk.server.netty.packet.OutPacket;
import net.tridentsdk.server.netty.packet.PacketType;

/**
 * TODO not an expert on this lol - AgentTroll
 *
 * @author The TridentSDK Team
 */
public class PacketStatusOutResponse extends OutPacket {
    Response response;

    public PacketStatusOutResponse() {
        response = new Response();
    }

    @Override
    public int getId() {
        return 0x00;
    }

    public Response getResponse() {
        return response;
    }

    @Override
    public void encode(ByteBuf buf) {
        String json = new GsonBuilder().create().toJson(response);
        Codec.writeString(buf, json);
    }

    @Override
    public PacketType getType() {
        return PacketType.OUT;
    }

    //TODO: Do this properly
    public static class Response {
        Version version = new Version();
        Players players = new Players();
        Description description = new Description();

        public static class Version {
            String name = "1.8";
            int protocol = 47;
        }

        public static class Players {
            int max = 10;
            int online = 5;
        }

        public static class Description {
            String text = "default blah blah this is never going to show";
        }
    }
}
