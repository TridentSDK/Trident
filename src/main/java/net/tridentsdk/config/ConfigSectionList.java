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
package net.tridentsdk.config;

import com.google.gson.JsonArray;
import net.tridentsdk.api.config.*;
import net.tridentsdk.api.config.ConfigSection;

import java.util.Collection;

// TODO: Javadoc

/**
 * Section of the config dedicated to storing values from a collection
 *
 * @author The TridentSDK Team
 */
public class ConfigSectionList<V> extends net.tridentsdk.api.config.ConfigList<V> {
    private static final long serialVersionUID = -5809487198383216782L;
    private final net.tridentsdk.api.config.ConfigSection parent;

    protected ConfigSectionList(net.tridentsdk.api.config.ConfigSection parent, JsonArray handle) {
        super(handle);
        this.parent = parent;
    }

    @Override
    public boolean add(V element) {
        boolean changed = super.add(element);

        if (changed) {
            this.jsonHandle.add(((net.tridentsdk.api.config.ConfigSection) element).asJsonObject());
        }

        return changed;
    }

    @Override
    public boolean addAll(Collection<? extends V> coll) {
        boolean changed = super.addAll(coll);

        if (changed) {
            for (V element : coll) {
                this.add(element);
            }
        }

        return changed;
    }

    /* (non-Javadoc)
     * @see java.util.ArrayList#set(int, java.lang.Object)
     */
    @Override
    public V set(int index, V element) {
        this.jsonHandle.set(index, ((net.tridentsdk.api.config.ConfigSection) element).asJsonObject());
        return super.set(index, element);
    }

    @Override
    public V remove(int index) {
        this.jsonHandle.remove(index);
        return super.remove(index);
    }

    @Override
    public boolean remove(Object element) {
        boolean success = super.remove(element);

        if (success) {
            this.jsonHandle.remove(((net.tridentsdk.api.config.ConfigSection) element).asJsonObject());
        }

        return success;
    }

    /* (non-Javadoc)
     * @see java.util.ArrayList#removeAll(java.util.Collection)
     */
    @Override
    public boolean removeAll(Collection<?> coll) {
        boolean changed = super.removeAll(coll);

        if (changed) {
            for (Object o : coll) {
                this.remove(o);
            }
        }

        return changed;
    }

    /* (non-Javadoc)
     * @see java.util.ArrayList#removeRange(int, int)
     */
    @Override
    protected void removeRange(int start, int end) {
        super.removeRange(start, end);

        for (int i = start; i < end; i++) {
            this.jsonHandle.remove(i);
        }
    }

    /* (non-Javadoc)
     * @see java.util.ArrayList#clear()
     */
    @Override
    public void clear() {
        super.clear();
        this.jsonHandle = new JsonArray();
    }

    protected ConfigSection getParent() {
        return this.parent;
    }
}
