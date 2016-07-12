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

import net.tridentsdk.config.ConfigSection;

import java.util.Collection;
import java.util.Set;

/**
 * Implementation of a configuration section
 */
public class TridentConfigSection implements ConfigSection {
    @Override
    public Set<String> keys() {
        return null;
    }

    @Override
    public Collection<Object> values() {
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
        return 0;
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
        return null;
    }

    @Override
    public void setString(String key, String value) {

    }

    @Override
    public Collection<?> getCollection(String key) {
        return null;
    }

    @Override
    public <T> Collection<T> getCollection(String key, Class<T> type) {
        return null;
    }

    @Override
    public void createSection(String name) {

    }

    @Override
    public ConfigSection getSection(String name) {
        return null;
    }

    @Override
    public Collection<ConfigSection> children() {
        return null;
    }

    @Override
    public ConfigSection rootSection() {
        return null;
    }

    @Override
    public ConfigSection parent() {
        return null;
    }
}
