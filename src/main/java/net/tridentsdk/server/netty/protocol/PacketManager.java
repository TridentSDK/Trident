package net.tridentsdk.server.netty.protocol;

import net.tridentsdk.server.netty.packet.Packet;
import net.tridentsdk.server.netty.packet.UnknownPacket;

import java.util.HashMap;
import java.util.Map;

abstract class PacketManager {
    protected Map<Integer, Class<?>> packets = new HashMap<>();

    protected PacketManager() {
        packets.put(-1, UnknownPacket.class);
    }

    public Packet getPacket(int id) {
        try{
            Class<?> cls = packets.get(id);

            if(cls == null)
                cls = packets.get(-1);

            return cls.asSubclass(Packet.class).newInstance();
        }catch(IllegalAccessException | InstantiationException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }
}