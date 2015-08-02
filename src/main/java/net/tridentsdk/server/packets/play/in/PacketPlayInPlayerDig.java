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

package net.tridentsdk.server.packets.play.in;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.base.Block;
import net.tridentsdk.base.BlockDirection;
import net.tridentsdk.base.Position;
import net.tridentsdk.base.Substance;
import net.tridentsdk.effect.particle.ParticleEffect;
import net.tridentsdk.effect.particle.ParticleEffectType;
import net.tridentsdk.entity.types.EntityType;
import net.tridentsdk.event.Cancellable;
import net.tridentsdk.event.Event;
import net.tridentsdk.event.block.BlockBreakEvent;
import net.tridentsdk.event.player.*;
import net.tridentsdk.inventory.inventory.Item;
import net.tridentsdk.registry.Registered;
import net.tridentsdk.server.entity.projectile.TridentArrow;
import net.tridentsdk.server.netty.ClientConnection;
import net.tridentsdk.server.netty.packet.InPacket;
import net.tridentsdk.server.netty.packet.Packet;
import net.tridentsdk.server.packets.play.out.PacketPlayOutBlockChange;
import net.tridentsdk.server.packets.play.out.PacketPlayOutParticle;
import net.tridentsdk.server.player.PlayerConnection;
import net.tridentsdk.server.player.TridentPlayer;
import net.tridentsdk.server.world.TridentChunk;
import net.tridentsdk.util.TridentLogger;
import net.tridentsdk.util.Vector;
import net.tridentsdk.world.settings.GameMode;

public class PacketPlayInPlayerDig extends InPacket {
    private short status;
    private Position location;
    private short blockFace;

    @Override
    public int id() {
        return 0x07;
    }

    public short status() {
        return this.status;
    }

    public Position location() {
        return this.location;
    }

    public short blockFace() {
        return this.blockFace;
    }

    @Override
    public Packet decode(ByteBuf buf) {
        this.status = (short) buf.readByte();
        long encodedLocation = buf.readLong();

        this.location = Position.create(null, (double) (encodedLocation >> 38),
                (double) (encodedLocation << 26 >> 52), (double) (encodedLocation << 38 >> 38));
        this.blockFace = (short) buf.readByte();

        return this;
    }

    @Override
    public void handleReceived(ClientConnection connection) {
        TridentPlayer player = ((PlayerConnection) connection).player();
        DigStatus digStatus = DigStatus.getStatus(this.status);
        BlockDirection face = null;

        if (digStatus == DigStatus.DIG_START && player.gameMode() == GameMode.CREATIVE) {
            digStatus = DigStatus.DIG_FINISH;
        }

        this.location.setWorld(player.world());

        switch (this.blockFace) {
            case 0:
                face = BlockDirection.BOTTOM;
                break;

            case 1:
                face = BlockDirection.TOP;
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
                TridentLogger.error(new IllegalArgumentException("Client sent invalid BlockFace!"));
        }

        Cancellable event = null;

        switch (digStatus) {
            case DIG_START:
            case DIG_CANCEL:
            case DIG_FINISH:
                event = new PlayerDigEvent(player, face, this.status);

                if(digStatus == DigStatus.DIG_FINISH) {
                    Block block = player.world().blockAt(location());
                    BlockBreakEvent blockBreak = new BlockBreakEvent(player, block, face, player.heldItem());

                    if(blockBreak.isIgnored())
                        return;
                }

                break;

            case DROP_ITEMSTACK:
                event = new PlayerDropItemEvent(player, null); // todo: spawn item and call the event
                break;

            case DROP_ITEM:
                event = new PlayerDropItemEvent(player, null);
                break;

            case SHOOT_ARROW:
                Item item = player.heldItem();
                if (item.type().isWeapon()) {
                    if (item.type() == Substance.BOW) // bow
                        event = new PlayerShootBowEvent(player, null);
                    else event = new PlayerInteractEvent(player, location.block()); // other weapons
                } else if (item.type().isEdible()) {
                    event = new PlayerConsumeEvent(player, item, 0.0);
                }
                // shoot bow, if player has a food item finish eating
                break;
            default:
        }

        Registered.events().fire((Event) event);

        if (event == null || event.isIgnored())
            return;

        // TODO act accordingly

        if (digStatus == DigStatus.SHOOT_ARROW) {
            TridentArrow entity = (TridentArrow) location.world().spawn(EntityType.ARROW, location);
            entity.setVelocity(player.position().asUnitVector());
        }

        if(digStatus == DigStatus.DIG_FINISH) {
            int[] arr = {location.block().substance().id() + (location.block().meta() << 12)};

            ((TridentChunk) location().chunk()).setAt(location, Substance.AIR, (byte) 0, (byte) 255, (byte) 15);
            TridentPlayer.sendAll(new PacketPlayOutBlockChange()
                    .set("location", location).set("blockId", Substance.AIR.id()));

            ParticleEffect effect = location.world().spawnParticle(ParticleEffectType.BLOCK_CRACK);
            effect.setCount(64);
            effect.setLongDistance(false);
            effect.setPosition(location.add(new Vector(0.5, 0.5, 0.5)).asVector());
            effect.setOffset(new Vector(0.45, 0.45, 0.45));
            effect.setData(arr);
            effect.apply();
        }
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
            for (DigStatus status : DigStatus.values()) {
                if (status.id == id) {
                    return status;
                }
            }

            return null;
        }

        public short getId() {
            return this.id;
        }
    }
}
