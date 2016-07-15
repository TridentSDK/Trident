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
import java.util.Collection;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Implementation of a configuration section
 */
public class TridentConfigSection implements ConfigSection {
    private final Map<String, JsonElement> elements = new ConcurrentSkipListMap<>((o1, o2) -> 0);

    public void write(JsonObject object) {
        elements.forEach(object::add);
    }

    public void read(JsonObject object) {
        object.entrySet().stream().forEach((e) -> elements.put(e.getKey(), e.getValue()));
    }

    @Override
    public ConfigSection rootSection() {
        return null;
    }

    @Override
    public ConfigSection parent() {
        return null;
    }

    @Override
    public void createChild(String name) {

    }

    @Nullable
    @Override
    public ConfigSection getChild(String name) {
        return null;
    }

    @Override
    public Collection<ConfigSection> children(boolean deep) {
        return null;
    }

    @Override
    public Set<String> keys(boolean deep) {
        return null;
    }

    @Override
    public Collection<Object> values(boolean deep) {
        return null;
    }

    @Override
    public Set<Map.Entry<String, Object>> entries(boolean deep) {
        return null;
    }

    @Override
    public Object get(String key) {
        return null;
    }

    @Override
    public <T> T get(String key, Class<T> type) {
        return null;
    }

    @Override
    public void set(String key, Object value) {

    }

    @Override
    public int getInt(String key) {
        return getElement(key).getAsInt();
    }

    @Override
    public void setInt(String key, int value) {

    }

    @Override
    public short getShort(String key) {
        return 0;
    }

    @Override
    public void setShort(String key, short value) {

    }

    @Override
    public long getLong(String key) {
        return 0;
    }

    @Override
    public void setLong(String key, long value) {

    }

    @Override
    public byte getByte(String key) {
        return 0;
    }

    @Override
    public void setByte(String key, byte value) {

    }

    @Override
    public float getFloat(String key) {
        return 0;
    }

    @Override
    public void setFloat(String key, float value) {

    }

    @Override
    public double getDouble(String key) {
        return 0;
    }

    @Override
    public void setDouble(String key, double value) {

    }

    @Override
    public char getChar(String key) {
        return 0;
    }

    @Override
    public void setChar(String key, char value) {

    }

    @Override
    public boolean getBoolean(String key) {
        return false;
    }

    @Override
    public void setBoolean(String key, boolean value) {

    }

    @Override
    public String getString(String key) {
        return getElement(key).getAsString();
    }

    @Override
    public void setString(String key, String value) {

    }

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

    private JsonElement getElement(String key) {
        String[] split = key.split(".");
        TridentConfigSection section = this;
        for (String s : split) {
            section = (TridentConfigSection) section.getChild(s);
            if (section == null) {
                throw new RuntimeException(new NoSuchElementException(
                        String.format("Section \"%s\" in your key \"%s\" cannot be found", s, key)));
            }
        }

        String finalKey = split[split.length - 1];
        JsonElement element = section.elements.get(finalKey);
        if (element == null) {
            throw new RuntimeException(new NoSuchElementException(
                    String.format("Key \"%s\" in your key \"%s\" cannot be found", finalKey, key)));
        }

        return element;
    }
}