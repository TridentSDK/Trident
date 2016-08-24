/*
 * Trident - A Multithreaded Server Alternative
 * Copyright 2016 The TridentSDK Team
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
package net.tridentsdk.server.packet.play;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.server.packet.PacketOut;

import javax.annotation.concurrent.Immutable;

/**
 * Sent after {@link PlayOutSpawnPos} to communicate to the
 * client their abilities once joined.
 */
@Immutable
public final class PlayOutAbilities extends PacketOut {
    public PlayOutAbilities() {
        super(PlayOutAbilities.class);
    }

    @Override
    public void write(ByteBuf buf) {
        byte abilities = 0x00;
        abilities |= 0x00; // invuln
        abilities |= 0x00 << 1; // flying
        abilities |= 0x00 << 2; // allow fly
        abilities |= 0x00 << 3; // creative mode

        buf.writeByte(abilities);
        buf.writeFloat(1.659F);
        buf.writeFloat(0.699999988079071F);
    }
}