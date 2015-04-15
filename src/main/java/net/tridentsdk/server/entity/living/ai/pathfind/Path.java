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

package net.tridentsdk.server.entity.living.ai.pathfind;

import net.tridentsdk.util.Vector;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@NotThreadSafe
public class Path {
    private final List<Node> pathPoints;
    private int index;
    private double step;

    public Path(Node end) {
        LinkedList<Node> trace = new LinkedList<>();

        while(end != null) {
            trace.add(end);
            end = end.parent();
        }

        Collections.reverse(trace); // Reverse list so that it isn't backwards
        this.pathPoints = new ArrayList<>(trace);
    }

    /**
     * Get the movement for the enxt tick based on the speed of the entity.
     *
     * @param speed of entity (blocks per tick).
     * @return Movement for entity in current tick
     */
    public Vector pollMovement(double speed) {
        if(speed > 1.0) {
            throw new IllegalArgumentException("Cannot move more than 1X/tick!");
        }

        Vector movement = new Vector(0, 0, 0);
        Node current = pathPoints.get(index);
        Node next = pathPoints.get(index + 1);
        double dx = next.x() - current.x();
        double dz = next.z() - current.z();

        if(speed + step >= 1.0) {
            movement.add(1 - step, 0, 1 - step);

            // Find next node
            index += 1;

            if(!finished()) {
                movement.setY(next.y() - current.y());

                // Recalculate values
                current = next;
                next = pathPoints.get(index + 1);
                dx = next.x() - current.x();
                dz = next.z() - current.z();
                this.step = 0.0;
            }
        }

        step += speed;
        movement.add(dx * speed, 0, dz * speed);
        return movement;
    }

    public boolean finished() {
        return index >= pathPoints.size() - 1;
    }
}
