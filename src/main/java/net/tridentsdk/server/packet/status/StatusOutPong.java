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
package net.tridentsdk.server.packet.status;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.server.packet.PacketOut;

/**
 * Server response to {@link StatusInPing}.
 */
public final class StatusOutPong extends PacketOut {
    /**
     * The time that was sent by the ping packet
     */
    private final long time;

    public StatusOutPong(long time) {
        super(StatusOutPong.class);
        this.time = time;
    }

    @Override
    public void write(ByteBuf buf) {
        // Schema:
        // long:time
        // fucking mojang won't make up their mind with
        // varlong and long and bs like that
        buf.writeLong(this.time);
    }
}