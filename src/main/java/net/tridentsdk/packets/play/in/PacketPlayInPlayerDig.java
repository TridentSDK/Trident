/*
 * Copyright (c) 2014, TridentSDK Team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * * Neither the name of TridentSDK nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */


package net.tridentsdk.packets.play.in;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.api.BlockFace;
import net.tridentsdk.api.Location;
import net.tridentsdk.api.event.Cancellable;
import net.tridentsdk.api.event.Event;
import net.tridentsdk.api.event.player.PlayerDigEvent;
import net.tridentsdk.api.event.player.PlayerDropItemEvent;
import net.tridentsdk.player.PlayerConnection;
import net.tridentsdk.player.TridentPlayer;
import net.tridentsdk.server.TridentServer;
import net.tridentsdk.server.netty.ClientConnection;
import net.tridentsdk.server.netty.packet.InPacket;
import net.tridentsdk.server.netty.packet.Packet;

public class PacketPlayInPlayerDig extends InPacket {
    private short status;
    private Location location;
    private short blockFace;

    @Override
    public int getId() {
        return 0x07;
    }

    public short getStatus() {
        return status;
    }

    public Location getLocation() {
        return location;
    }

    public short getBlockFace() {
        return blockFace;
    }

    @Override
    public Packet decode(ByteBuf buf) {
        this.status = buf.readByte();
        long encodedLocation = buf.readLong();

        this.location = new Location(null, (double) (encodedLocation >> 38), (double) (encodedLocation << 26 >> 52),
                (double) (encodedLocation << 38 >> 38));
        this.blockFace = buf.readByte();

        return this;
    }

    @Override
    public void handleReceived(ClientConnection connection) {
        TridentPlayer player = ((PlayerConnection) connection).getPlayer();
        DigStatus digStatus = DigStatus.getStatus(status);
        BlockFace face = null;

        switch(blockFace) {
            case 0:
                face = BlockFace.BOTTOM;
                break;

            case 1:
                face = BlockFace.TOP;
                break;

            case 2:
                // z--
                break;

            case 3:
                // z++
                break;

            case 4:
                // x--
                break;

            case 5:
                // x++
                break;

            default:
                throw new IllegalArgumentException("Client sent invalid BlockFace!");
        }

        Cancellable event = null;

        switch(digStatus) {
            case DIG_START:
            case DIG_CANCEL:
            case DIG_FINISH:
                event = new PlayerDigEvent(player, face, status);
                break;

            case DROP_ITEMSTACK:
                event = new PlayerDropItemEvent(player, null); // todo: spawn item and call the event
                break;

            case DROP_ITEM:
                event = new PlayerDropItemEvent(player, null);
                break;

            case SHOOT_ARROW:
                // shoot bow, if player has a food item finish eating
                break;
        }

        TridentServer.getInstance().getEventManager().call((Event) event);

        if(event == null || event.isCancelled())
            return;



        location.setWorld(player.getWorld());
    }

    public enum DigStatus {
        DIG_START(0),
        DIG_CANCEL(1),
        DIG_FINISH(2),
        DROP_ITEMSTACK(3),
        DROP_ITEM(4),
        SHOOT_ARROW(5);

        private final short id;

        DigStatus(int id) {
           this.id = (short) id;
        }

        public static DigStatus getStatus(short id) {
            for(DigStatus status : values()) {
                if(status.id == id) {
                    return status;
                }
            }

            return null;
        }

        public short getId() {
            return id;
        }
    }
}
