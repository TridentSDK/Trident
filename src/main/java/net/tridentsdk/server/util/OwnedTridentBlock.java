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
package net.tridentsdk.server.util;

import net.tridentsdk.base.Block;
import net.tridentsdk.server.player.TridentPlayer;
import net.tridentsdk.server.world.TridentBlock;

/**
 * Represents a block which holds data about the placer
 *
 * @author The TridentSDK Team
 */
public class OwnedTridentBlock extends TridentBlock {
    private final TridentPlayer player;

    public OwnedTridentBlock(TridentPlayer player, Block block) {
        super(block.position());
        this.player = player;
    }

    /**
     * Obtains the place
     *
     * @return
     */
    public TridentPlayer player() {
        return this.player;
    }
}
