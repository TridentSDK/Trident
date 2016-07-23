/*
 * Trident - A Multithreaded Server Alternative
 * Copyright 2016 The TridentSDK Team
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
package net.tridentsdk.server.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.tridentsdk.config.ConfigSection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import java.util.Collection;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Implementation of a configuration section
 */
@ThreadSafe
public class TridentConfigSection implements ConfigSection {
    /**
     * The separator which splits section name keys
     */
    public static final String SECTION_SEPARATOR = Pattern.quote(".");

    /**
     * Put in the place of the element map instead of any value
     */
    private static final Object SECTION_PLACEHOLDER = new Object();

    // One map holds the elements of this section, while the
    // other holds references to the children of this
    // section
    private final ConcurrentLinkedStringMap<Object> elements = new ConcurrentLinkedStringMap<>();
    private final ConcurrentLinkedStringMap<ConfigSection> children = new ConcurrentLinkedStringMap<>();

    /**
     * Writes the config section and all of the associated
     * children to a new json object.
     *
     * @return this section and all its children's json
     */
    public JsonObject write() {
        JsonObject object = new JsonObject();

        // funky code because we need to make sure all the
        // elements remain in insertion order
        elements.forEach((k, v) -> {
            if (v == SECTION_PLACEHOLDER) {
                TridentConfigSection section = ((TridentConfigSection) children.get(k));
                v = section.write();
            }

            object.add(k, ConfigIo.asJson(v));
        });
        return object;
    }

    /**
     * Loads the json from file into memory
     *
     * @param object the file json
     */
    public void read(JsonObject object) {
        object.entrySet().stream().forEach(e -> {
            String key = e.getKey();
            JsonElement value = e.getValue();

            // special handling for json objects which are
            // config sections
            if (value.isJsonObject()) {
                TridentConfigSection section = (TridentConfigSection) createChild(key);
                section.read(value.getAsJsonObject());
            }

            elements.put(key, ConfigIo.asObj(value, TridentAdapter.class));
        });
    }

    /**
     * The name of this config section (empty if root)
     */
    private final String name;
    /**
     * The parent config section, or null if root
     */
    private final ConfigSection parent;
    /**
     * The root config section, or null if root
     */
    private final ConfigSection root;

    /**
     * Creates a new config section.
     *
     * @param name   the name of the new config section
     * @param parent the parent of the child section
     * @param root   the root section
     */
    public TridentConfigSection(String name, ConfigSection parent, ConfigSection root) {
        this.name = name;
        this.parent = parent;
        this.root = root;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public ConfigSection root() {
        return this.root;
    }

    @Override
    public ConfigSection parent() {
        return this.parent;
    }

    @Override
    public ConfigSection createChild(String name) {
        TridentConfigSection section = new TridentConfigSection(name, this, root());
        elements.put(name, SECTION_PLACEHOLDER);
        children.put(name, section);
        return section;
    }

    @Nullable
    @Override
    public ConfigSection getChild(String name) {
        return children.get(name);
    }

    @Override
    public Collection<ConfigSection> children(boolean deep) {
        return children.values();
    }

    @Override
    public boolean removeChild(String name) {
        // use bitwise AND in order to force both operations
        // even if the first operation was false
        return children.remove(name) != null & elements.remove(name) != null;
    }

    @Override
    public Set<String> keys(boolean deep) {
        return elements.keySet();
    }

    @Override
    public Collection<Object> values(boolean deep) {
        return elements.values();
    }

    @Override
    public Set<Map.Entry<String, Object>> entries(boolean deep) {
        return elements.entrySet();
    }

    @Override
    public Object get(String key) {
        return getElement(key);
    }

    @Override
    public <T> T get(String key, Class<T> type) {
        return (T) getElement(key);
    }

    @Override
    public void set(String key, Object value) {
        // TODO handle types
        String[] split = key.split(SECTION_SEPARATOR);
        String finalKey = key;

        TridentConfigSection section = this;
        if (split.length > 0) {
            finalKey = split[split.length - 1];
            for (int i = 0; i < split.length - 1; i++) {
                section = (TridentConfigSection) section.createChild(split[i]);
            }
        }

        section.elements.put(finalKey, ConfigIo.asJson(value));
    }

    @Override
    public boolean remove(String key) {
        String[] split = key.split(SECTION_SEPARATOR);
        String finalKey = split.length == 0 ? key : split[split.length - 1];
        TridentConfigSection section = findSection(split);
        return section.elements.remove(finalKey) != null;
    }

    @Override
    public boolean has(String key) {
        return elements.containsKey(key);
    }

    @Override
    public int getInt(String key) {
        return ((Number) getElement(key)).intValue();
    }

    @Override
    public void setInt(String key, int value) {
        set(key, value);
    }

    @Override
    public short getShort(String key) {
        return ((Number) getElement(key)).shortValue();
    }

    @Override
    public void setShort(String key, short value) {
        set(key, value);
    }

    @Override
    public long getLong(String key) {
        return ((Number) getElement(key)).longValue();
    }

    @Override
    public void setLong(String key, long value) {
        set(key, value);
    }

    @Override
    public byte getByte(String key) {
        return ((Number) getElement(key)).byteValue();
    }

    @Override
    public void setByte(String key, byte value) {
        set(key, value);
    }

    @Override
    public float getFloat(String key) {
        return ((Number) getElement(key)).floatValue();
    }

    @Override
    public void setFloat(String key, float value) {
        set(key, value);
    }

    @Override
    public double getDouble(String key) {
        return ((Number) getElement(key)).doubleValue();
    }

    @Override
    public void setDouble(String key, double value) {
        set(key, value);
    }

    @Override
    public char getChar(String key) {
        return (char) getElement(key);
    }

    @Override
    public void setChar(String key, char value) {
        set(key, value);
    }

    @Override
    public boolean getBoolean(String key) {
        return (boolean) getElement(key);
    }

    @Override
    public void setBoolean(String key, boolean value) {
        set(key, value);
    }

    @Override
    public String getString(String key) {
        return (String) getElement(key);
    }

    @Override
    public void setString(String key, String value) {
        set(key, value);
    }

    // TODO collections
    // TODO hashmaps

    @Nonnull
    @Override
    public Collection<?> getCollection(String key) {
        return null;
    }

    @Nonnull
    @Override
    public <T> Collection<T> getCollection(String key, Class<T> type) {
        return null;
    }

    /**
     * Obtains a config section with a key split with .
     *
     * @param split the split key
     * @return the config section
     */
    @Nonnull
    private TridentConfigSection findSection(String[] split) {
        TridentConfigSection section = this;
        if (split.length > 0) {
            for (int i = 0; i < split.length - 1; i++) {
                String sectionName = split[i];
                section = (TridentConfigSection) section.getChild(sectionName);
                if (section == null) {
                    throw new RuntimeException(new NoSuchElementException(
                            String.format("Section \"%s\" cannot be found", sectionName)));
                }
            }
        }

        return section;
    }

    /**
     * Obtains the element given the . split key
     *
     * @param key the key at which to find the element
     * @return the element
     */
    @Nonnull
    private Object getElement(String key) {
        String[] split = key.split(SECTION_SEPARATOR);
        String finalKey = key;

        TridentConfigSection section = findSection(split);
        if (section != this) {
            finalKey = split[split.length - 1];
        }

        // if all goes well, we have the final key at the
        // last element
        // try to get the value from the last child section
        // before the final key
        // if null, throw exception
        Object element = section.elements.get(finalKey);
        if (element == null) {
            throw new RuntimeException(new NoSuchElementException(
                    String.format("Key \"%s\" in your key \"%s\" cannot be found", finalKey, key)));
        }

        return element;
    }
}