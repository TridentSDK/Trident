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
 * Sent by the server to the client whenever the indicated
 * game state should change, such as for an invalid bed,
 * raining, or game mode toggles.
 */
@Immutable
public final class PlayOutGameState extends PacketOut {
    private final int reason;
    private final float val;

    public PlayOutGameState(int reason, float val) {
        super(PlayOutGameState.class);
        this.reason = reason;
        this.val = val;
    }

    @Override
    public void write(ByteBuf buf) {
        buf.writeByte(this.reason);
        buf.writeFloat(this.val);
    }
}
