/*
 * Trident - A Multithreaded Server Alternative
 * Copyright 2017 The TridentSDK Team
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
 * Players update the server time on the client. This packet
 * should only be used to resync the world time with the
 * client time.
 */
@Immutable
public final class PlayOutTime extends PacketOut {
    private final long age;
    private final long currentTime;

    public PlayOutTime(long age, long currentTime) {
        super(PlayOutTime.class);
        this.age = age;
        this.currentTime = currentTime;
    }

    @Override
    public void write(ByteBuf buf) {
        buf.writeLong(this.age);
        buf.writeLong(this.currentTime);
    }
}