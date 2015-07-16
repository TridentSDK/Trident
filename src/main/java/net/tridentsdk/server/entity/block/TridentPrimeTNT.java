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

package net.tridentsdk.server.entity.block;

import net.tridentsdk.Handler;
import net.tridentsdk.Position;
import net.tridentsdk.base.Audio;
import net.tridentsdk.base.Block;
import net.tridentsdk.base.Substance;
import net.tridentsdk.entity.types.EntityType;
import net.tridentsdk.entity.block.PrimeTNT;
import net.tridentsdk.event.entity.EntityExplodeEvent;
import net.tridentsdk.server.data.RecordBuilder;
import net.tridentsdk.server.packets.play.out.PacketPlayOutExplosion;
import net.tridentsdk.server.packets.play.out.PacketPlayOutSoundEffect;
import net.tridentsdk.server.player.TridentPlayer;
import net.tridentsdk.server.world.TridentChunk;
import net.tridentsdk.util.Vector;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class TridentPrimeTNT extends TridentFallingBlock implements PrimeTNT {
    private volatile int fuse = 80; // 4 seconds by default
    private volatile int radius = 4; // Sphere with 4 blocks radius

    private volatile AtomicInteger countDown = new AtomicInteger(fuse);

    public TridentPrimeTNT(UUID id, Position spawnLocation) {
        super(id, spawnLocation);
    }

    @Override
    protected void doTick() {
        if (countDown.get() == 0) {
            EntityExplodeEvent event = new EntityExplodeEvent(this, radius);
            Handler.forEvents().fire(event);
            if (event.isCancelled()) {
                return;
            }

            Position p = getPosition();
            int radius = this.radius; // Prevent the value from changing within operation

            int minX = (int) (p.getX() - radius);
            int maxX = (int) (p.getX() + radius);
            int minY = (int) (p.getY() - radius);
            int maxY = (int) (p.getY() + radius);
            int minZ = (int) (p.getZ() - radius);
            int maxZ = (int) (p.getZ() + radius);

            RecordBuilder[] records = new RecordBuilder[(int) Math.pow(radius * 2, 3)];
            int recordIdx = 0;

            for (int i = minX; i < maxX; i++) {
                for (int j = minY; j < maxY; j++) {
                    for (int k = minZ; k < maxZ; k++) {
                        Block block = p.getWorld().getBlockAt(new Position(p.getWorld(), i, j, k));
                        ((TridentChunk) p.getWorld().getChunkAt(i / 16, k / 16, false))
                                .setAt(i, j, k, Substance.AIR, (byte) 0, (byte) 0, (byte) 0);
                        records[recordIdx] = new RecordBuilder()
                                .setX((byte) i)
                                .setY((byte) j)
                                .setZ((byte) k)
                                .setData(block.getMeta())
                                .setBlockId(block.getSubstance().getID());
                        recordIdx++;
                    }
                }
            }

            PacketPlayOutExplosion explosion = new PacketPlayOutExplosion();
            explosion.set("loc", p)
                    .set("recordCount", records.length)
                    .set("records", records)
                    .set("velocity", new Vector(radius, radius, radius));

            PacketPlayOutSoundEffect sound = new PacketPlayOutSoundEffect();
            sound.set("sound", Audio.RANDOM_EXPLODE).set("loc", p).set("volume", 50).set("pitch", 20);

            TridentPlayer.sendAll(explosion);
            TridentPlayer.sendAll(sound);
        }

        countDown.decrementAndGet();
    }

    @Override
    public int getExplodeTicks() {
        return fuse;
    }

    @Override
    public int getRadius() {
        return radius;
    }

    @Override
    public void setRadius(int radius) {
        this.radius = radius;
    }

    @Override
    public void getExplodeTicks(int ticks) {
        this.fuse = ticks;
        this.countDown.set(ticks);
    }

    @Override
    public EntityType getType() {
        return EntityType.PRIMED_TNT;
    }
}
