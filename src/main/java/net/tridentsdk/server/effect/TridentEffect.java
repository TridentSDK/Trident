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
package net.tridentsdk.server.effect;

import net.tridentsdk.effect.Effect;
import net.tridentsdk.entity.living.Player;
import net.tridentsdk.server.netty.packet.OutPacket;
import net.tridentsdk.server.player.TridentPlayer;

public abstract class TridentEffect<T> implements Effect<T> {

    @Override
    public void apply(){
        TridentPlayer.sendAll(getPacket());
    }

    @Override
    public void apply(Player player){
        ((TridentPlayer) player).connection().sendPacket(getPacket());
    }

    @Override
    public void applyToEveryoneExcept(Player player){
        TridentPlayer.sendFiltered(getPacket(), p -> !p.equals(player));
    }

    public abstract OutPacket getPacket();

}
