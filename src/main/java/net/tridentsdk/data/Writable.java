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
package net.tridentsdk.data;

import io.netty.buffer.ByteBuf;

/**
 * A metadata abstraction that allows the data to be written to provided {@link io.netty.buffer.ByteBuf}
 *
 * @author The TridentSDK Team
 */
public interface Writable {
    /**
     * Writes the data contained by the current implementation into serialized form in the provided {@link
     * io.netty.buffer.ByteBuf}
     *
     * <p>Check the implementation source to see serialization spec</p>
     *
     * @param buf the buffer to write the serialized form to
     */
    void write(ByteBuf buf);
}
