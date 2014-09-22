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

package net.tridentsdk.server.netty.protocol;

import net.tridentsdk.server.netty.packet.*;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

abstract class PacketManager {
    protected final Map<Integer, Class<? extends Packet>> inPackets = new HashMap<>();
    protected final Map<Integer, Class<? extends Packet>> outPackets = new HashMap<>();

    PacketManager() {
        this.inPackets.put(-1, UnknownPacket.class);
        this.outPackets.put(-1, UnknownPacket.class);
    }

    public Packet getPacket(int id, PacketType type) {
        try {
            Map<Integer, Class<? extends Packet>> applicableMap;

            switch (type) {
                case IN:
                    applicableMap = this.inPackets;
                    break;

                case OUT:
                    applicableMap = this.outPackets;
                    break;

                default:
                    return null;
            }

            Class<?> cls = applicableMap.get(id);

            if (cls == null)
                cls = applicableMap.get(-1);

            return cls.asSubclass(Packet.class).getConstructor().newInstance();
        } catch (IllegalAccessException | InstantiationException |
                NoSuchMethodException | InvocationTargetException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }
}