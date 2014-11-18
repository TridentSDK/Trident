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
package net.tridentsdk.api.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;

// TODO: Javadoc

/**
 * Used for advanced interaction with the Config API for custom serializing/deserializing of Java Objects
 *
 * @author The TridentSDK Team
 */
public final class GsonFactory {
    private static final GsonBuilder builder = new GsonBuilder().setPrettyPrinting();
    private static Gson gson = builder.create();

    private GsonFactory() {
    }

    public static void registerTypeAdapter(Type type, Object adapter) {
        builder.registerTypeAdapter(type, adapter);
        gson = builder.create();
    }

    protected static Gson getGson() {
        return gson;
    }
}
