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
import net.tridentsdk.server.inventory.TridentInventory;
import net.tridentsdk.server.net.NetClient;
import net.tridentsdk.server.packet.PacketIn;

import javax.annotation.concurrent.Immutable;

@Immutable
public class PlayInCloseWindow extends PacketIn {
    public PlayInCloseWindow() {
        super(PlayInCloseWindow.class);
    }

    @Override
    public void read(ByteBuf buf, NetClient client) {
        short windowId = buf.readUnsignedByte();
        if (windowId == 0) {
            return; // Player inventory, no close needed
        }

        TridentInventory.close(windowId, client.getPlayer());
    }
}
