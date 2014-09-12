/*
 * Copyright (c) 2014, The TridentSDK Team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     1. Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 *     2. Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 *     3. Neither the name of TridentSDK nor the names of its
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

package net.tridentsdk.entity;

import net.tridentsdk.api.Location;
import net.tridentsdk.api.Material;
import net.tridentsdk.api.entity.Entity;
import net.tridentsdk.api.entity.EntityType;
import net.tridentsdk.api.util.Vector;
import net.tridentsdk.api.world.World;

import java.util.List;

public class TridentEntity implements Entity {

    protected volatile Vector velocity;
    protected volatile boolean velocityChanged;

    protected volatile Location loc;
    protected volatile boolean locationChanged;

    protected volatile boolean onGround;
    protected volatile double fallDistance;

    protected volatile long ticksLived;

    protected Entity passenger;
    protected int id;

    public TridentEntity(int id, Location spawnLocation) {
        this.id = id;

        this.velocity = new Vector(0D, 0D, 0D);
        this.velocityChanged = false;

        this.loc = spawnLocation;
        this.locationChanged = false;

        for(double y = loc.getY(); y > 0; y--) {
            Location l = new Location(loc.getWorld(), loc.getX(),
                    y, loc.getZ());

            if (l.getWorld().getBlockAt(l).getType() != Material.AIR) {
                this.fallDistance = (loc.getY() - y);
                this.onGround = (fallDistance == 0D);

                break;
            }
        }

        this.ticksLived = 0L;
        this.passenger = null;
    }

    @Override
    public void teleport(double x, double y, double z) {
        teleport(new Location(getWorld(), x, y, z));
    }

    @Override
    public void teleport(Entity entity) {
        teleport(entity.getLocation());
    }

    @Override
    public void teleport(Location location) {
        this.loc = location;
        this.locationChanged = true;
    }

    @Override
    public World getWorld() {
        return loc.getWorld();
    }

    @Override
    public Location getLocation() {
        return loc;
    }

    @Override
    public Vector getVelocity() {
        return velocity;
    }

    @Override
    public void setVelocity(Vector vector) {
        this.velocity = vector;
        this.velocityChanged = true;

        // TODO: update all clients
    }

    @Override
    public void tick() {

    }

    @Override
    public boolean isOnGround() {
        return onGround;
    }

    /**
     * TODO
     * @param radius the spherical radius to look for entities around
     * @return
     */
    @Override
    public List<Entity> getNearbyEntities(double radius) {
        return null;
    }

    @Override
    public int getId() {
        return id;
    }

    /**
     * TODO
     */
    @Override
    public void remove() {
    }

    @Override
    public Entity getPassenger() {
        return passenger;
    }

    @Override
    public void setPassenger(Entity entity) {
        this.passenger = entity;

        // TODO: Update clients
    }

    @Override
    public void eject() {
        //
    }

    @Override
    public EntityType getType() {
        return null;
    }
}
