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
package net.tridentsdk.server.effect.visual;

import net.tridentsdk.base.Position;
import net.tridentsdk.effect.visual.VisualEffect;
import net.tridentsdk.effect.visual.VisualEffectType;
import net.tridentsdk.server.effect.TridentRemoteEffect;
import net.tridentsdk.server.netty.packet.OutPacket;
import net.tridentsdk.server.packets.play.out.PacketPlayOutEffect;
import net.tridentsdk.util.Vector;
import net.tridentsdk.world.World;

public class TridentVisualEffect extends TridentRemoteEffect<VisualEffectType> implements VisualEffect {

    private PacketPlayOutEffect packet = new PacketPlayOutEffect();

    public TridentVisualEffect(World world){
        packet.set("loc", new Position(world, 0, 0, 0));
    }

    @Override
    public VisualEffectType type(){
        return null; // TODO Add revers lookup map
    }

    @Override
    public void setType(VisualEffectType type){
        packet.set("status", type);
    }

    @Override
    public void setData(int data){
        packet.set("data", data);
    }

    @Override
    public int data(){
        return packet.data();
    }

    @Override
    public void setPosition(Vector vector){
        packet.location().setX(vector.x());
        packet.location().setY(vector.y());
        packet.location().setZ(vector.z());
    }

    @Override
    public OutPacket getPacket(){
        return packet;
    }

}
