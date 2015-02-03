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

import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import net.tridentsdk.docs.Volatile;
import net.tridentsdk.server.netty.Codec;
import net.tridentsdk.util.ArrayTool;

import java.util.Collection;

/**
 * Builds the property attributes for entity packets
 *
 * @author The TridentSDK Team
 */
public class PropertyBuilder implements Writable {
    private String key;
    private double value;
    @Volatile(policy = "Do not write individual elements", reason = "Thread safe array", fix = "See Line 114")
    private volatile String[] modifiers;

    /**
     * Creates a 0 length property array
     */
    public PropertyBuilder() {
        this.modifiers = new String[] { };
    }

    /**
     * Creates a property array with the specified size
     *
     * @param size the size of the property array
     */
    public PropertyBuilder(int size) {
        this.modifiers = new String[size];
    }

    /**
     * The key of the property
     *
     * @return the property key
     */
    public String key() {
        return this.key;
    }

    /**
     * Sets the key of the property builder
     *
     * @param key the key to set
     * @return the current instance
     */
    public PropertyBuilder setKey(String key) {
        this.key = key;

        return this;
    }

    /**
     * Gets the value of the property builder
     *
     * @return the value of this property
     */
    public double value() {
        return this.value;
    }

    /**
     * Sets the value of the property builder
     *
     * @param value the value to set
     * @return the current instance
     */
    public PropertyBuilder setValue(double value) {
        this.value = value;

        return this;
    }

    /**
     * Gets the property array wrapped by the builder
     *
     * @return the property array
     */
    public String[] modifiers() {
        return this.modifiers.clone();
    }

    /**
     * Puts an attribute property at the given index
     *
     * @param index    the index to place the property at
     * @param modifier the property to place
     * @return the current instance
     */
    public PropertyBuilder addModifier(int index, String modifier) {
        String[] modifiers = this.modifiers;
        modifiers[index] = modifier;
        String[] read = this.modifiers; // Flush caches, make entire array visible

        return this;
    }

    /**
     * Removes all null elements in the property array
     *
     * @return the current instance
     */
    public PropertyBuilder cleanup() {
        Collection<String> list = Lists.newArrayList();

        for (String value : this.modifiers) {
            if (value != null) {
                list.add(value);
            }
        }

        this.modifiers = ArrayTool.using(list.toArray()).convertTo(String.class);
        return this;
    }

    @Override
    public void write(ByteBuf buf) {
        this.cleanup();

        Codec.writeString(buf, this.key);
        buf.writeDouble(this.value);
        Codec.writeVarInt32(buf, this.modifiers.length);

        for (String s : this.modifiers)
            Codec.writeString(buf, s);
    }
}
