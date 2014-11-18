/*
 *     TridentSDK - A Minecraft Server API
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
package net.tridentsdk.nbt.builder;

import net.tridentsdk.nbt.*;

/**
 * @author The TridentSDK Team
 */
public class CompoundTagBuilder<B> {
    private final CompoundTag current;
    private final B parentBuilder;
    private TagContainer parent;

    protected CompoundTagBuilder(CompoundTag tag, B parentBuilder) {
        this.parentBuilder = parentBuilder;
        this.current = tag;
    }

    public CompoundTagBuilder(String name, TagContainer parent, B parentBuilder) {
        this.parent = parent;
        this.parentBuilder = parentBuilder;
        this.current = new CompoundTag(name);
        parent.addTag(this.current);
    }

    public CompoundTagBuilder<CompoundTagBuilder<B>> beginCompoundTag(String name) {
        return new CompoundTagBuilder<>(name, this.current, this);
    }

    public B endCompoundTag() {
        return this.parentBuilder;
    }

    public ListTagBuilder<CompoundTagBuilder<B>> beginListTag(String name, TagType type) {
        return new ListTagBuilder<>(name, this.current, this, type);
    }

    public CompoundTagBuilder<B> compoundTag(CompoundTag value) {
        this.current.addTag(value);
        return this;
    }

    public CompoundTagBuilder<B> listTag(ListTag tag) {
        this.current.addTag(tag);
        return this;
    }

    public CompoundTagBuilder<B> nullTag(String name) {
        this.current.addTag(new NullTag(name));
        return this;
    }

    public CompoundTagBuilder<B> byteArrayTag(String name, byte... value) {
        this.current.addTag(new ByteArrayTag(name).setValue(value));
        return this;
    }

    public CompoundTagBuilder<B> byteTag(String name, byte value) {
        this.current.addTag(new ByteTag(name).setValue(value));
        return this;
    }

    public CompoundTagBuilder<B> doubleTag(String name, double value) {
        this.current.addTag(new DoubleTag(name).setValue(value));
        return this;
    }

    public CompoundTagBuilder<B> floatTag(String name, float value) {
        this.current.addTag(new FloatTag(name).setValue(value));
        return this;
    }

    public CompoundTagBuilder<B> intArrayTag(String name, int... value) {
        this.current.addTag(new IntArrayTag(name).setValue(value));
        return this;
    }

    public CompoundTagBuilder<B> intTag(String name, int value) {
        this.current.addTag(new IntTag(name).setValue(value));
        return this;
    }

    public CompoundTagBuilder<B> longTag(String name, long value) {
        this.current.addTag(new LongTag(name).setValue(value));
        return this;
    }

    public CompoundTagBuilder<B> shortTag(String name, short value) {
        this.current.addTag(new ShortTag(name).setValue(value));
        return this;
    }

    public CompoundTagBuilder<B> stringTag(String name, String value) {
        this.current.addTag(new StringTag(name).setValue(value));
        return this;
    }
}
