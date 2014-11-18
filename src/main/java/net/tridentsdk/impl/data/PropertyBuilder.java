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
package net.tridentsdk.impl.data;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.impl.netty.Codec;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Builds the property attributes for entity packets
 *
 * @author The TridentSDK Team
 */
public class PropertyBuilder implements Writable {
    private String key;
    private double value;
    private volatile String[] modifiers;

    /**
     * Creates a 0 length property array
     */
    public PropertyBuilder() {
        this.modifiers = new String[]{};
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
    public String getKey() {
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
     * @return the value (whatever that might be) TODO
     */
    public double getValue() {
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
    public String[] getModifiers() {
        return this.modifiers;
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
        Collection<String> list = new ArrayList<>();

        for (String value : this.modifiers) {
            if (value != null) {
                list.add(value);
            }
        }

        this.modifiers = (String[]) list.toArray(); // Not even sure if this will work...
        return this;
    }

    @Override
    public void write(ByteBuf buf) {
        this.cleanup();

        Codec.writeString(buf, this.key);
        buf.writeDouble(this.value);
        Codec.writeVarInt32(buf, this.modifiers.length);

        for (String s : this.modifiers) Codec.writeString(buf, s);
    }
}
