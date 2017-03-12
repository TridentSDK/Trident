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
import net.tridentsdk.base.Position;
import net.tridentsdk.base.Substance;
import net.tridentsdk.server.net.NetClient;
import net.tridentsdk.server.net.NetData;
import net.tridentsdk.server.packet.PacketIn;

/**
 * @author TridentSDK
 * @since 0.5-alpha
 */
public class PlayInPlayerDig extends PacketIn {

    public PlayInPlayerDig() {
        super(PlayInPlayerDig.class);
    }

    @Override
    public void read(ByteBuf buf, NetClient client) {
        DigStatus status = DigStatus.values()[NetData.rvint(buf)];
        // Single thread usage of the vector uses a noninflated lock,
        // no optimization needed here
        Position position = NetData.rvec(buf).toPosition(client.getPlayer().getWorld());
        DigFace face = DigFace.get(buf.readByte());
        
        // TODO Hack prevention, events
    
        position.getBlock().setSubstance(Substance.AIR);
    }
    
    private enum DigStatus {
        
        START_DIGGING,
        CANCEL_DIGGING,
        FINISH_DIGGING,
        DROP_ITEM_STACK,
        DROP_ITEM,
        SHOOT_ARROW_FINISH_EATING,
        SWAP_ITEM_IN_HAND
        
    }
    
    private enum DigFace {
        
        BOTTOM,
        TOP,
        NORTH,
        SOUTH,
        WEST,
        EAST,
        SPECIAL;
        
        public static DigFace get(byte face){
            if(face <= 5){
                return values()[face];
            }
            
            return SPECIAL;
        }
        
    }

}
