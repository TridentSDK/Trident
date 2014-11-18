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
