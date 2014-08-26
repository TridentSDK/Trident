/*
 * Copyright (C) 2014 The TridentSDK Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.tridentsdk.server.netty.protocol;

import net.tridentsdk.server.netty.packet.Packet;
import net.tridentsdk.server.netty.packet.PacketType;
import net.tridentsdk.server.netty.packet.UnknownPacket;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

abstract class PacketManager {
    protected final Map<Integer, Class<?>> inPackets = new HashMap<>();
    protected final Map<Integer, Class<?>> outPackets = new HashMap<>();

    protected PacketManager() {
        this.inPackets.put(-1, UnknownPacket.class);
        this.outPackets.put(-1, UnknownPacket.class);
    }

    public Packet getPacket(int id, PacketType type) {
        try {
            Map<Integer, Class<?>> applicableMap;

            switch(type) {
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