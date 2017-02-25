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
import net.tridentsdk.entity.living.Player;
import net.tridentsdk.server.packet.PacketOut;
import net.tridentsdk.server.player.TridentPlayer;
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
    private final boolean canFly;
    private final GameMode gameMode;
    private final float flyingSpeed;
    private final float walkingSpeed;

    private boolean doubleJump;

    public PlayOutPlayerAbilities(TridentPlayer player) {
        super(PlayOutPlayerAbilities.class);
        this.isGod = player.isGodMode();
        this.isFlying = player.isFlying();
        this.canFly = player.canFly();
        this.gameMode = player.getGameMode();
        this.flyingSpeed = player.getFlyingSpeed();
        this.walkingSpeed = player.getWalkingSpeed();
        this.doubleJump = false;
    }

    public void setDoubleJumpInsteadOfFlying() {
        this.doubleJump = canFly;
    }

    @Override
    public void write(ByteBuf buf) {
        byte abilities = 0x00;
        abilities |= this.isGod ? 0x01 : 0x00; // invuln
        abilities |= this.isFlying && !this.doubleJump ? 0x02 : 0; // flying
        abilities |= this.canFly ? 0x04 : 0; // can fly
        abilities |= this.gameMode == GameMode.CREATIVE ? 0x08 : 0; // creative

        buf.writeByte(abilities);
        buf.writeFloat(flyingSpeed);
        buf.writeFloat(walkingSpeed);
    }
}
