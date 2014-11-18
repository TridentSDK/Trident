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

import java.util.ArrayList;
import java.util.List;

/**
 * @author The TridentSDK Team
 */
public class ListTag extends NBTTag implements TagContainer {
    final List<NBTTag> tags = new ArrayList<>();
    final TagType innerType;

    public ListTag(String name, TagType innerType) {
        super(name);
        this.innerType = innerType;
    }

    public List<NBTTag> listTags() {
        return Lists.newArrayList(this.tags);
    }

    public NBTTag getTag(int index) {
        return this.tags.get(index);
    }

    public void clearTags() {
        this.tags.clear();
    }

    public boolean containsTag(NBTTag tag) {
        return this.tags.contains(tag);
    }

    @Override
    public void addTag(NBTTag tag) {
        if (tag.getType() == this.innerType) {
            this.tags.add(tag);
        }
    }

    public void removeTag(NBTTag tag) {
        this.tags.remove(tag);
    }

    public TagType getInnerType() {
        return this.innerType;
    }

    /* (non-Javadoc)
     * @see net.tridentsdk.api.nbt.NBTTag#getType()
     */
    @Override
    public TagType getType() {
        // TODO Auto-generated method stub
        return TagType.LIST;
    }
}
