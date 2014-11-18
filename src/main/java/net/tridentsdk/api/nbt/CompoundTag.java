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
package net.tridentsdk.api.nbt;

import com.google.common.collect.Lists;

import java.util.HashMap;
import java.util.Map;

/**
 * @author The TridentSDK Team
 */
public class CompoundTag extends NBTTag implements TagContainer {
    final Map<String, NBTTag> tags = new HashMap<>(); //Hashmap for quick lookup with names

    public CompoundTag(String name) {
        super(name);
    }

    public Iterable<NBTTag> listTags() {
        return Lists.newArrayList(this.tags.values());
    }

    public boolean containsTag(String name) {
        return this.tags.containsKey(name);
    }

    public NBTTag getTag(String name) {
        return this.tags.containsKey(name) ? this.tags.get(name) : new NullTag(name);
    }

    public <T extends NBTTag> T getTagAs(String name) {
        return (T) getTag(name);
    }

    @Override
    public void addTag(NBTTag tag) {
        this.tags.put(tag.getName(), tag);
    }

    public void removeTag(String name) {
        this.tags.remove(name);
    }

    public void clearTags() {
        this.tags.clear();
    }

    /* (non-Javadoc)
     * @see net.tridentsdk.api.nbt.NBTTag#getType()
     */
    @Override
    public TagType getType() {
        return TagType.COMPOUND;
    }
}
