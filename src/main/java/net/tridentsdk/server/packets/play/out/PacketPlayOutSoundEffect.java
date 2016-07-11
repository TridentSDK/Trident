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

package net.tridentsdk.server.packets.play.out;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.base.Position;
import net.tridentsdk.effect.sound.SoundEffectType;
import net.tridentsdk.server.netty.Codec;
import net.tridentsdk.server.netty.packet.OutPacket;

public class PacketPlayOutSoundEffect extends OutPacket {

    protected SoundEffectType sound;
    protected Position loc;
    protected float volume; // f * 100
    protected int pitch; // 63 = 100%

    @Override
    public int id() {
        return 0x46;
    }

    public SoundEffectType sound() {
        return this.sound;
    }

    public Position location() {
        return this.loc;
    }

    public float volume() {
        return this.volume;
    }

    public int pitch() {
        return this.pitch;
    }

    @Override
    public void encode(ByteBuf buf) {
        Codec.writeString(buf, this.sound.toString());

        buf.writeInt((int) this.loc.x());
        buf.writeInt((int) this.loc.y());
        buf.writeInt((int) this.loc.z());

        buf.writeFloat(this.volume);
        buf.writeByte(this.pitch);
    }
}
