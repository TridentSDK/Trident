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

package net.tridentsdk.server.entity.decorate;

import net.tridentsdk.entity.Entity;
import net.tridentsdk.entity.LivingEntity;
import net.tridentsdk.entity.decorate.LivingDecorationAdapter;
import net.tridentsdk.server.packets.play.out.PacketPlayOutParticle;
import net.tridentsdk.server.player.TridentPlayer;
import net.tridentsdk.util.Vector;

/**
 * Represents a LivingEntity that has an age and has the ability to bread
 *
 * @author TridentSDK Team
 */
public abstract class DecoratedAgeable extends LivingDecorationAdapter {
    private int age;

    protected DecoratedAgeable(LivingEntity entity) {
        super(entity);
    }

    /**
     * The current age of this entity, in ticks
     *
     * @return the age of this entity
     */
    public int getAge() {
        return age;
    }

    /**
     * Set the current age of this entity, in ticks
     *
     * @param ticks the age to set
     */
    public void setAge(int ticks) {
        this.age = ticks;
    }

    /**
     * Whether or not this entity can breed or not, where the ability to breed represents whether or not this entity
     * can
     * become "in love"
     *
     * @return whether or not this entity can be bred
     */
    public abstract boolean canBreed();

    /**
     * Whether or not this entity is "in love", such that it will actively display the particle effect for breeding
     * hearts and search for a mate
     *
     * @return whether or not this entity is in love
     */
    public abstract boolean isInLove();

    public void applyBreed() {
        // TODO breed child
    }

    public void applyLove(Entity other) {
        if (!isInLove())
            return;
        PacketPlayOutParticle particle = new PacketPlayOutParticle();
        particle.set("particleId", 34)
                .set("distance", false)
                .set("loc", original().headLocation().add(new Vector(0, 1, 0)))
                .set("offset", new Vector(0, 1, 0))
                .set("particleData", 0.0F)
                .set("data", new int[0]);
        TridentPlayer.sendAll(particle);
        // TODO love the other entity
    }
}
