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
package net.tridentsdk.api.config;

import com.google.gson.JsonArray;

import java.util.Collection;

// TODO: Javadoc

/**
 * Section of the config dedicated to storing values from a collection
 *
 * @author The TridentSDK Team
 */
public class ConfigSectionList<V> extends ConfigList<V> {
    private static final long serialVersionUID = -5809487198383216782L;
    private final ConfigSection parent;

    protected ConfigSectionList(ConfigSection parent, JsonArray handle) {
        super(handle);
        this.parent = parent;
    }

    @Override
    public boolean add(V element) {
        boolean changed = super.add(element);

        if (changed) {
            this.jsonHandle.add(((ConfigSection) element).asJsonObject());
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
        this.jsonHandle.set(index, ((ConfigSection) element).asJsonObject());
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
            this.jsonHandle.remove(((ConfigSection) element).asJsonObject());
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
