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

package net.tridentsdk.server.entity.ai.pathfinder;

import net.tridentsdk.Position;
import net.tridentsdk.base.Block;
import net.tridentsdk.base.Substance;
import net.tridentsdk.entity.Entity;
import net.tridentsdk.util.Vector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Pathfinder {
    private final Entity entity;
    private final Node start;
    private final Node end;
    private final double range;

    private final HashSet<Node> openList = new HashSet<>();
    private final HashSet<Node> closedList = new HashSet<>();
    private Path result;

    public Pathfinder(Entity entity, Position target, double range) {
        this.entity = entity;
        this.start = new Node(null, entity.position());
        this.end = new Node(null, target);
        this.range = range;
    }

    public void find() {
        // Possibly run this on its own thread and let the entity wait until getResult != null
        openList.add(start);
        closedList.add(start);
        while(!openList.isEmpty()) {
            Node current = getBestTile();
            openList.remove(current);
            for(Node neighbor : getNeighbors(current)) {
                closedList.add(neighbor);
                if(start.distance(neighbor) > range * range || !canWalkThrough(neighbor)) {
                    continue;
                }

                Node floor;
                if(!canWalkThrough(neighbor)) {
                    // Floor isn't really the floor anymore, its now the block above the neighbor
                    if(canWalkThrough(floor = neighbor.getRelative(0, 1, 0))) {
                        neighbor = floor;
                        neighbor.setParent(current);
                    } else {
                        continue;
                    }
                } else if(!canWalkOn(floor = neighbor.getRelative(0, -1, 0))) {
                    if(canWalkThrough(floor) && canWalkOn(floor.getRelative(0, -1, 0))) {
                        // We can down ^-^
                        neighbor = floor;
                    } else {
                        continue;
                    }
                }

                openList.add(neighbor);
                if(end.equals(neighbor)) {
                    // We found our destination!
                    end.setParent(current);
                    openList.clear();
                    break;
                }

                neighbor.calculateHGF(end);
            }
        }

        if(end.getParent() != null) {
            this.result = new Path(end);
        }
    }

    public Path getResult() {
        return result;
    }

    private List<Node> getNeighbors(Node current) {
        List<Node> list = new ArrayList<>();
        for(int x = -1; x <= 1; x++) {
            for(int z = -1; z <= 1; z++) {
                if(x == 0 && z == 0) {
                    continue;
                }

                Node neighbor = current.getRelative(x, 0, z);
                if(!closedList.contains(neighbor)) {
                    list.add(neighbor);
                }
            }
        }

        return list;
    }

    private Node getBestTile() {
        Node best = null;
        double bestScore = Double.MAX_VALUE;
        for(Node node : openList) {
            if(node.getF() < bestScore) {
                best = node;
                bestScore = node.getF();
            }
        }

        return best;
    }

    private boolean canWalkOn(Node node) {
        Block block = Position.create(entity.world(), node.getX(), node.getY(), node.getZ()).block();
        return block.substance().isSolid();
    }

    private boolean canWalkThrough(Node node) {
        Block block = Position.create(entity.world(), node.getX(), node.getY(), node.getZ()).block();
        return canWalkThrough(block.substance()) && canWalkThrough(block.relativeBlock(new Vector(0, 1, 0)).substance());
    }

    private boolean canWalkThrough(Substance type) {
        return type == Substance.AIR || type == Substance.LONG_GRASS || type == Substance.DEAD_BUSH || type == Substance.SAPLING
                || type == Substance.RED_ROSE || type == Substance.YELLOW_FLOWER || type == Substance.VINE;
    }
}
