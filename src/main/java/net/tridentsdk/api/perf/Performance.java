/*
 *     Trident - A Multithreaded Server Alternative
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
