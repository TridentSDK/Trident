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
package net.tridentsdk.server.effect.sound;

import net.tridentsdk.base.Position;
import net.tridentsdk.effect.sound.SoundEffect;
import net.tridentsdk.effect.sound.SoundEffectType;
import net.tridentsdk.server.effect.TridentRemoteEffect;
import net.tridentsdk.server.netty.packet.OutPacket;
import net.tridentsdk.server.packets.play.out.PacketPlayOutSoundEffect;
import net.tridentsdk.util.Vector;
import net.tridentsdk.world.World;

public class TridentSoundEffect extends TridentRemoteEffect<SoundEffectType> implements SoundEffect {

    private PacketPlayOutSoundEffect packet = new PacketPlayOutSoundEffect();

    public TridentSoundEffect(World world){
        packet.set("loc", new Position(world, 0, 0, 0));
    }

    @Override
    public SoundEffectType type(){
        return packet.sound();
    }

    @Override
    public void setType(SoundEffectType type){
        packet.set("sound", type);
    }

    @Override
    public void setPosition(Vector vector){
        packet.location().setX(vector.x());
        packet.location().setY(vector.y());
        packet.location().setZ(vector.z());
    }

    @Override
    public void setVolume(float volume){
        packet.set("volume", volume);
    }

    @Override
    public void setPitch(int pitch){
        packet.set("pitch", pitch);
    }

    @Override
    public float volume(){
        return packet.volume();
    }

    @Override
    public int pitch(){
        return packet.pitch();
    }

    @Override
    public OutPacket getPacket(){
        return packet;
    }

}
