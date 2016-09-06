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
import net.tridentsdk.server.net.NetData;
import net.tridentsdk.server.packet.PacketOut;
import net.tridentsdk.server.player.TridentPlayer;

/**
 * @author TridentSDK
 * @since 0.5-alpha
 */
public class PlayOutAnimation extends PacketOut {

    private TridentPlayer player;
    private AnimationType animationType;

    public PlayOutAnimation(TridentPlayer player, AnimationType animationType) {
        super(PlayOutAnimation.class);
        this.player = player;
        this.animationType = animationType;
    }

    @Override
    public void write(ByteBuf buf) {
        NetData.wvint(buf, player.getId());
        buf.writeByte(animationType.ordinal());
    }

    public enum AnimationType {

        SWING_MAIN_ARM,
        TAKE_DAMAGE,
        LEAVE_BED,
        SWING_OFFHAND,
        CRITICAL_EFFECT,
        MAGIC_CRITICAL_EFFECT;

    }

}
