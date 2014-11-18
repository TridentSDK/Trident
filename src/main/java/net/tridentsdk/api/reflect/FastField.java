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
package net.tridentsdk.api.reflect;

import com.esotericsoftware.reflectasm.FieldAccess;

import java.lang.reflect.Field;

/*
 * @NotJavaDoc
 * NOTE: This class only applies to any field which is not private
 */
public class FastField {
    private final FieldAccess access;
    private final String field;
    private final Object instance;

    FastField(Object instance, FieldAccess access, String field) {
        this.access = access;
        this.field = field;
        this.instance = instance;
    }

    public void set(Object value) {
        this.access.set(this.instance, this.field, value);
    }

    public <T> T get() {
        return (T) this.access.get(this.instance, this.field);
    }

    public Field toField() {
        try {
            return this.instance.getClass().getDeclaredField(this.field);
        } catch (NoSuchFieldException ignored) {
        }

        return null;
    }
}
