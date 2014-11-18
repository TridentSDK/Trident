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
package net.tridentsdk.reflect;

import com.esotericsoftware.reflectasm.ConstructorAccess;
import com.esotericsoftware.reflectasm.FieldAccess;
import com.esotericsoftware.reflectasm.MethodAccess;
import net.tridentsdk.api.reflect.FastConstructor;
import net.tridentsdk.api.reflect.FastField;
import net.tridentsdk.api.reflect.FastMethod;

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
