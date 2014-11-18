/*
 *     TridentSDK - A Minecraft Server API
 *     Copyright (C) 2014, The TridentSDK Team
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */


package net.tridentsdk.event.player;

import net.tridentsdk.api.BlockFace;
import net.tridentsdk.api.entity.living.Player;
import net.tridentsdk.api.event.Cancellable;
import net.tridentsdk.api.event.player.*;

public class PlayerDigEvent extends net.tridentsdk.api.event.player.PlayerEvent implements Cancellable {

    private final BlockFace face;
    private final short status;

    private boolean cancelled;

    public PlayerDigEvent(Player player, BlockFace face, short status) {
        super(player);

        this.face = face;
        this.status = status;
    }

    public BlockFace getFace() {
        return this.face;
    }

    public short getStatus() {
        return this.status;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
