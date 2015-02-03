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
     * @param buf the buffer to toPacket the serialized form to
     */
    void write(ByteBuf buf);
}
