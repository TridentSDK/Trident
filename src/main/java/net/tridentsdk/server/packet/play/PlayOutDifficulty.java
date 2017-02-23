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
import net.tridentsdk.world.World;
import net.tridentsdk.world.opt.Difficulty;

import javax.annotation.concurrent.Immutable;

/**
 * This packet is sent after {@link PlayOutPluginMsg} to
 * communicate the current difficulty on the player's
 * world.
 */
@Immutable
public final class PlayOutDifficulty extends PacketOut {
    /**
     * The world difficulty
     */
    private final Difficulty difficulty;

    public PlayOutDifficulty(World world) {
        super(PlayOutDifficulty.class);
        this.difficulty = world.getWorldOptions().getDifficulty();
    }

    @Override
    public void write(ByteBuf buf) {
        buf.writeByte(this.difficulty.asByte());
    }
}
