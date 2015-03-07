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

public class Node {
    private Node parent;
    private final int x;
    private final int y;
    private final int z;

    private double h = -1;
    private double g = -1;
    private double f = -1;

    public Node(Node parent, Position position) {
        this(parent, position.x(), position.y(), position.z());
    }

    public Node(Node parent, double x, double y, double z) {
        this(parent, (int) x, (int) y, (int) z);
    }

    public Node(Node parent, int x, int y, int z) {
        this.parent = parent;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Get f value of node.
     *
     * @return F value
     */
    public double getF() {
        return f;
    }

    /**
     * Get node relative to current.
     *
     * @param dx relative x
     * @param dy relative y
     * @param dz relative z
     * @return Relative node with current as parent
     */
    public Node getRelative(int dx, int dy, int dz) {
        return new Node(this, x + dx, y + dy, z + dz);
    }

    /**
     * Get the node x coord.
     *
     * @return X coord of node
     */
    public int getX() {
        return x;
    }

    /**
     * Get the node y coord
     *
     * @return Y coord of node
     */
    public int getY() {
        return y;
    }

    /**
     * Get the node z coord
     *
     * @return Z coord of node
     */
    public int getZ() {
        return z;
    }

    /**
     * Set the node's parent.
     *
     * @param parent New parent of node
     */
    public void setParent(Node parent) {
        this.parent = parent;
    }

    /**
     * Get the current node parent.
     *
     * @return Parent of node
     */
    public Node getParent() {
        return parent;
    }

    /**
     * Calculate distance cost based on euclidean distance.
     *
     * @param target Final tile in path
     */
    public void calculateH(Node target) {
        this.h = distance(target);
    }

    /**
     * Calculate movement cost, we dislike jumping and moving diagonally.
     */
    public void calculateG() {
        double dx = Math.abs(x - parent.x);
        double dy = Math.abs(y - parent.y);
        double dz = Math.abs(z - parent.z);
        this.g = parent.g; // Append to G value of parent

        // We dislike jumping because we are lazy
        if(dx == 1 && dy == 1 && dz == 1) {
            g += 1.7;
        } else if((dx == 1 && dz == 1) || dy == 1) {
            g += 1.4;
        } else {
            g += 1.0;
        }
    }

    /**
     * Calculate F value, basically H + G.
     */
    public void calculateF() {
        this.f = h + g;
    }

    /**
     * Calculate all movement values in one go.
     *
     * @param target Final tile in path
     */
    public void calculateHGF(Node target) {
        calculateH(target);
        calculateG();
        calculateF();
    }

    /**
     * Distance squared between this node and another
     *
     * @param with Node to compare with
     * @return Squared distance
     */
    public double distance(Node with) {
        int dx = with.x - x;
        int dy = with.y - y;
        int dz = with.z - z;
        return dx * dx + dy * dy + dz * dz;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;

        Node node = (Node) o;

        if(x != node.x) return false;
        if(y != node.y) return false;
        if(z != node.z) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        result = 31 * result + z;
        return result;
    }

    @Override
    public String toString() {
        return "Tile{" +
                "parent=" + parent +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
