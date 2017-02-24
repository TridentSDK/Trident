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
import net.tridentsdk.world.opt.GameMode;

import javax.annotation.concurrent.Immutable;

/**
 * Sent after {@link PlayOutSpawnPos} to communicate to the
 * client their abilities once joined.
 */
@Immutable
public final class PlayOutPlayerAbilities extends PacketOut {
    private final boolean isGod;
    private final boolean isFlying;
    private final GameMode gameMode;
    private final float flyingSpeed;
    private final float walkingSpeed;

    public PlayOutPlayerAbilities(boolean isGod, boolean isFlying, GameMode gameMode) {
        this(isGod, isFlying, gameMode, 0.159F, 0.699999988079071F);
    }

    public PlayOutPlayerAbilities(boolean isGod, boolean isFlying, GameMode gameMode, float flyingSpeed, float walkingSpeed) {
        super(PlayOutPlayerAbilities.class);
        this.isGod = isGod;
        this.isFlying = isFlying;
        this.gameMode = gameMode;
        this.flyingSpeed = flyingSpeed;
        this.walkingSpeed = walkingSpeed;
    }

    @Override
    public void write(ByteBuf buf) {
        byte abilities = 0x00;
        abilities |= this.isGod ? 0x01 : 0x00; // invuln
        abilities |= this.isFlying ? 0x01 << 1 : 0; // flying

        boolean creative = this.gameMode == GameMode.CREATIVE;
        abilities |= creative ? 0x01 << 2 : 0; // allow fly
        abilities |= creative ? 0x01 << 3 : 0; // creative mode

        buf.writeByte(abilities);
        buf.writeFloat(flyingSpeed);
        buf.writeFloat(walkingSpeed);
    }
}
