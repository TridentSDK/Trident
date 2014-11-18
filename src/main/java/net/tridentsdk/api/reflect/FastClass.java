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

import com.esotericsoftware.reflectasm.ConstructorAccess;
import com.esotericsoftware.reflectasm.FieldAccess;
import com.esotericsoftware.reflectasm.MethodAccess;

import java.lang.reflect.Field;

public class FastClass {

    private final Class<?> cls;

    private final FieldAccess fieldAccess;
    private final MethodAccess methodAccess;
    private final ConstructorAccess constructorAccess;

    private FastClass(Class<?> cls) {
        this.cls = cls;
        this.fieldAccess = FieldAccess.get(cls);
        this.methodAccess = MethodAccess.get(cls);
        this.constructorAccess = ConstructorAccess.get(cls);
    }

    public static FastClass get(Class<?> cls) {
        return new FastClass(cls);
    }

    public static FastClass get(Object obj) {
        return get(obj.getClass());
    }

    /**
     * Get a field from the class
     *
     * @param instance Instance of the class
     * @param name     Name of the field
     * @return FastField instance
     */
    public FastField getField(Object instance, String name) {
        return new FastField(instance, this.fieldAccess, name);
    }

    /**
     * Get a method from the class
     *
     * @param instance Instance of the class
     * @param name     Name of the method
     * @return FastMethod instance
     */
    public FastMethod getMethod(Object instance, String name) {
        return new FastMethod(instance, this.methodAccess, name);
    }

    public FastField[] getFields(Object instance) {
        Field[] fields = this.cls.getFields();
        FastField[] fastFields = new FastField[fields.length];

        for (int i = 0; i < fields.length; i += 1) {
            fastFields[i] = new FastField(instance, this.fieldAccess, fields[i].getName());
        }

        return fastFields;
    }

    public FastField[] getFields() {
        return this.getFields(null);
    }

    /**
     * Get the default constructor found
     *
     * @return the default FastConstructor
     */
    public FastConstructor getConstructor() {
        return new FastConstructor(this.constructorAccess);
    }

    public Class<?> toClass() {
        return this.cls;
    }
}
