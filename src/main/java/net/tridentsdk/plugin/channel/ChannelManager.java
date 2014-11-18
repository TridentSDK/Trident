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


package net.tridentsdk.plugin.channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ChannelManager {
    private static final ChannelManager INSTANCE = new ChannelManager();

    private final Map<String, PluginChannel> channels = new ConcurrentHashMap<>();

    private ChannelManager() {
    }

    public static ChannelManager getInstance() {
        return INSTANCE;
    }

    public void registerChannel(String name, PluginChannel channel) {
        if (this.channels.containsKey(name)) {
            throw new IllegalArgumentException("Channel " + name + " is already registered!");
        }

        this.channels.put(name, channel);
    }

    public void unregisterChannel(String name) {
        this.channels.remove(name);
    }

    public PluginChannel getPluginChannel(String name) {
        return this.channels.get(name);
    }
}
