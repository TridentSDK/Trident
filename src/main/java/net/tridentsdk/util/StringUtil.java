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
package net.tridentsdk.util;

public final class StringUtil {

    private StringUtil() {
    }

    /**
     * A for-loop efficient method for concating strings (or in some cases objects)
     *
     * @param objects Objects you wish to concat into a String
     * @return Built string
     */
    public static String concat(Object... objects) {
        StringBuilder builder = new StringBuilder();

        for (Object o : objects) {
            builder.append(o);
        }

        return builder.toString();
    }

    public static boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
        } catch (NumberFormatException ex) {
            return false;
        }

        return true;
    }
}
