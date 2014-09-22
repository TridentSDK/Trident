/*
 * Copyright (c) 2014, The TridentSDK Team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     1. Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *     2. Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *     3. Neither the name of the The TridentSDK Team nor the
 *        names of its contributors may be used to endorse or promote products
 *        derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL The TridentSDK Team BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.tridentsdk.entity;

/**
 * Enum to state entity status in PacketPlayOutEntityStatus
 *
 * @see net.tridentsdk.packets.play.out.PacketPlayOutEntityStatus
 */
public enum EntityStatus {

    ENTITY_HURT(2),
    ENTITY_DEAD(3),
    GOLEM_MASH(4),
    ANIMAL_TAMING(6),
    ANIMAL_TAMED(7),
    WOLF_SHAKING(8),
    EATING_ACCEPTED(9),
    SHEEP_EAT(10),
    GOLEM_HANDLING(11),
    VILLAGER_MATING(12),
    VILLAGER_ANGRY(13),
    VILLAGER_HAPPY(14),
    WITCH_ANIMATION(15),
    ZOMBIE_SHAKE(16), // to indicate that the zombie is converting into a Villager
    FIREWORK_EXPLODE(17),
    ANIMAL_LOVE(18);

    private final byte b;

    EntityStatus(int i) {
        this.b = (byte) i;
    }

    public byte toByte() {
        return this.b;
    }
}
