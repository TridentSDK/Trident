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
package net.tridentsdk.impl.entity;

/**
 * Enum to state entity status in PacketPlayOutEntityStatus
 *
 * @author The TridentSDK Team
 * @see net.tridentsdk.impl.packets.play.out.PacketPlayOutEntityStatus
 */
public enum EntityStatus {
    /**
     * Entity has been hurt
     */
    ENTITY_HURT(2),
    /**
     * Entity has died
     */
    ENTITY_DEAD(3),
    /**
     * An Iron Golem smashes an (unfortunate) mob
     */
    GOLEM_MASH(4),
    /**
     * A wolf is being tamed
     */
    ANIMAL_TAMING(6),
    /**
     * A wolf that has been tamed
     */
    ANIMAL_TAMED(7),
    /**
     * A wolf that is shaking water off
     */
    WOLF_SHAKING(8),
    /**
     * Occurs after food is consumed
     */
    EATING_ACCEPTED(9),
    /**
     * Sheep bends down to eat grass
     */
    SHEEP_EAT(10),
    /**
     * An Iron Golem picks an entity up
     */
    GOLEM_HANDLING(11),
    /**
     * A villager mates
     */
    VILLAGER_MATING(12),
    /**
     * A villager is displeased by something
     */
    VILLAGER_ANGRY(13),
    /**
     * The villager is pleased by something
     */
    VILLAGER_HAPPY(14),
    /**
     * Something the witch does
     */
    WITCH_ANIMATION(15),
    /**
     * When a villager-zombie is cured and turns back into a villager
     */
    ZOMBIE_SHAKE(16), // to indicate that the zombie is converting into a Villager
    /**
     * When a firework destroys itself and effects follow
     */
    FIREWORK_EXPLODE(17),
    /**
     * Animals like each other
     */
    ANIMAL_LOVE(18);

    private final byte b;

    EntityStatus(int i) {
        this.b = (byte) i;
    }

    /**
     * Gets the ID value representing the status
     *
     * @return the {@code byte} ID value
     */
    public byte toByte() {
        return this.b;
    }
}
