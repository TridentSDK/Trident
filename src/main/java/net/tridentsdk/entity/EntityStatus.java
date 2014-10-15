/*
 *     Trident - A Multithreaded Server Alternative
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
package net.tridentsdk.entity;

/**
 * Enum to state entity status in PacketPlayOutEntityStatus
 *
 * @author The TridentSDK Team
 * @see net.tridentsdk.packets.play.out.PacketPlayOutEntityStatus
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
