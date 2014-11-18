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
package net.tridentsdk.api.perf;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public final class Performance {
    private static final Unsafe UNSAFE = initUnsafe();

    private Performance() {}

    private static Unsafe initUnsafe() {
        try {
            Field field = findField(Unsafe.class, "theUnsafe");
            field.setAccessible(true);
            return (Unsafe) field.get(null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Unsafe getUnsafe() {
        return UNSAFE;
    }

    private static Field findField(Class<?> c, String name) {
        Field field = null;
        try {
            field = c.getDeclaredField(name);
            field.setAccessible(true);
        } catch (NoSuchFieldException e) {
            System.out.println("Field " + name + " cannot be found in " + c);
            e.printStackTrace();
        }

        return field;
    }

    public static UnsafeReflector wrap(final String field) {
        return new UnsafeReflector() {
            private final Field local = findField(getCaller(), field);

            @Override
            public long address() {
                if (Modifier.isStatic(local.getModifiers()))
                    return UNSAFE.staticFieldOffset(local);
                return UNSAFE.objectFieldOffset(local);
            }

            private Class<?> getCaller() {
                try {
                    return Class.forName(Thread.currentThread().getStackTrace()[4].getClassName());
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        };
    }
}
