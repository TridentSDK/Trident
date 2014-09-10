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

package net.tridentsdk.data;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.server.netty.Codec;

public class PropertyBuilder {

    private String key;
    private double value;
    private volatile String[] modifiers; // TODO: look more into this, modify accordingly

    public PropertyBuilder() {
        modifiers = new String[] {};
    }

    public String getKey() {
        return key;
    }

    public PropertyBuilder setKey(String key) {
        this.key = key;

        return this;
    }

    public double getValue() {
        return value;
    }

    public PropertyBuilder setValue(double value) {
        this.value = value;

        return this;
    }

    public String[] getModifiers() {
        return modifiers;
    }

    public PropertyBuilder addModifier(int index, String modifier) {
        modifiers[index] = modifier;

        return this;
    }

    public PropertyBuilder cleanup() {
        String[] newModifiers = new String[] {};

        for(String value : modifiers) {
            if(value != null) {
                newModifiers[newModifiers.length] = value;
            }
        }

        this.modifiers = newModifiers;
        return this;
    }

    public PropertyBuilder write(ByteBuf buf) {
        cleanup();

        Codec.writeString(buf, key);
        buf.writeDouble(value);
        Codec.writeVarInt32(buf, modifiers.length);

        for(String s : modifiers) {
            Codec.writeString(buf, s);
        }

        return this;
    }
}
