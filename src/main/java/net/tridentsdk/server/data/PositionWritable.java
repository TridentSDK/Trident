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

package net.tridentsdk.server.data;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.base.Position;

/**
 * Represents a writable form of a {@link Position}
 *
 * @author The TridentSDK Team
 */
public class PositionWritable implements Writable {
    private Position loc;

    /**
     * Creates a new position based from an existing location
     *
     * @param loc the location to wrap with writable format
     */
    public PositionWritable(Position loc) {
        this.loc = loc;
    }

    /**
     * Gets the wrapped, original location
     *
     * @return the location passed in by constructor or by {@link #setLoc(Position)}
     */
    public Position location() {
        return this.loc;
    }

    /**
     * Sets the wrapped position
     *
     * <p>This does not change the value of already written locations. This is purely for purposes of performance,
     * but removes concurrency.</p>
     *
     * @param loc the location to wrap with writable format
     */
    public void setLoc(Position loc) {
        this.loc = loc;
    }

    @Override
    public void write(ByteBuf buf) {
        buf.writeLong((long) ((int) this.loc.x() & 0x3FFFFFF) << 38 |
                (long) ((int) this.loc.y() & 0xFFF) << 26 |
                (long) ((int) this.loc.z() & 0x3FFFFFF));
    }
}
