/*
 * Copyright (c) 2014, TridentSDK Team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * * Neither the name of TridentSDK nor the names of its
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

package net.tridentsdk.data;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.server.netty.Codec;

public class PropertyBuilder implements Writable {
    private String key; // What are these 2 fields?
    private double value;
    private volatile String[] modifiers; // Ignore volatile array warning, already fixed

    public PropertyBuilder() {
        this.modifiers = new String[]{};
    }

    public PropertyBuilder(int size) {
        this.modifiers = new String[size];
    }

    public String getKey() {
        return this.key;
    }

    public PropertyBuilder setKey(String key) {
        this.key = key;

        return this;
    }

    public double getValue() {
        return this.value;
    }

    public PropertyBuilder setValue(double value) {
        this.value = value;

        return this;
    }

    public String[] getModifiers() {
        return this.modifiers;
    }

    public PropertyBuilder addModifier(int index, String modifier) {
        String[] modifiers = this.modifiers;
        modifiers[index] = modifier;
        String[] read = this.modifiers; // Flush caches, make entire array visible

        return this;
    }

    public PropertyBuilder cleanup() {
        String[] newModifiers = {}; // What? 0 length array for what?

        for (String value : this.modifiers) {
            if (value != null) {
                newModifiers[newModifiers.length] = value;
            }
        }

        this.modifiers = newModifiers;
        String[] read = this.modifiers;
        return this;
    }

    @Override
    public void write(ByteBuf buf) {
        this.cleanup();

        Codec.writeString(buf, this.key);
        buf.writeDouble(this.value);
        Codec.writeVarInt32(buf, this.modifiers.length);

        for (String s : this.modifiers) {
            Codec.writeString(buf, s);
        }
    }
}
