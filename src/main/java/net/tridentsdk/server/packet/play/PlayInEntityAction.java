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
import net.tridentsdk.server.net.NetClient;
import net.tridentsdk.server.net.NetData;
import net.tridentsdk.server.packet.PacketIn;
import net.tridentsdk.server.player.TridentPlayer;

/**
 * @author TridentSDK
 * @since 0.5-alpha
 */
public class PlayInEntityAction extends PacketIn {

    public PlayInEntityAction() {
        super(PlayInEntityAction.class);
    }

    @Override
    public void read(ByteBuf buf, NetClient client) {
        int entityId = NetData.rvint(buf);
        int actionId = NetData.rvint(buf);
        int jumpBoost = NetData.rvint(buf);

        TridentPlayer player = client.getPlayer();
        switch (actionId) {
            case 0: // start crouching
                player.setCrouching(true);
                break;
            case 1: // stop crouching
                player.setCrouching(false);
                break;
            case 2: // leave bed
                // TODO
                break;
            case 3: // start sprinting
                player.setSprinting(true);
                break;
            case 4: // stop sprinting
                player.setSprinting(false);
                break;
            case 5: // start jump w/ horse
                // TODO
                break;
            case 6: // stop jump w/ horse
                // TODO
                break;
            case 7: // open horse inventory
                // TODO
                break;
            case 8: // start flying w/ elytra
                // TODO
                break;
            default:
                break;
        }

        player.updateMetadata();
    }

}
