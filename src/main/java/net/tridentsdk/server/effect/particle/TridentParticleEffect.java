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
package net.tridentsdk.server.effect.particle;

import net.tridentsdk.base.Position;
import net.tridentsdk.effect.particle.ParticleEffect;
import net.tridentsdk.effect.particle.ParticleEffectType;
import net.tridentsdk.server.effect.TridentRemoteEffect;
import net.tridentsdk.server.netty.packet.OutPacket;
import net.tridentsdk.server.packets.play.out.PacketPlayOutParticle;
import net.tridentsdk.util.Vector;
import net.tridentsdk.world.World;

public class TridentParticleEffect extends TridentRemoteEffect<ParticleEffectType> implements ParticleEffect {

    private PacketPlayOutParticle packet = new PacketPlayOutParticle();

    public TridentParticleEffect(World world){
        packet.set("loc", new Position(world, 0, 0, 0));
    }

    @Override
    public ParticleEffectType type(){
        return packet.particle();
    }

    @Override
    public void setType(ParticleEffectType type){
        packet.set("particle", type);
    }

    @Override
    public void setPosition(Vector vector){
        packet.location().setX(vector.x());
        packet.location().setY(vector.y());
        packet.location().setZ(vector.z());
    }

    @Override
    public void setCount(int count){
        packet.set("count", count);
    }

    @Override
    public void setLongDistance(boolean longDistance){
        packet.set("distance", longDistance);
    }

    @Override
    public void setData(int[] data){
        packet.set("data", data);
    }

    @Override
    public void setOffset(Vector offset){
        packet.set("offset", offset);
    }

    @Override
    public int count(){
        return packet.count();
    }

    @Override
    public boolean longDistance(){
        return packet.isDistance();
    }

    @Override
    public int[] data(){
        return packet.data();
    }

    @Override
    public Vector offset(){
        return packet.offset();
    }

    @Override
    public OutPacket getPacket(){
        return packet;
    }

}
