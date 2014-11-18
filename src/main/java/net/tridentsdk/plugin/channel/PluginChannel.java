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

import net.tridentsdk.Trident;

import java.util.ArrayList;
import java.util.List;

public abstract class PluginChannel {
    private final List<Byte[]> history = new ArrayList<>();

    public void process(byte... message) {
        if (!Trident.isTrident()) {
            throw new UnsupportedOperationException("Only TridentSDK is allowed to execute this method!");
        }

        Byte[] bytes = new Byte[message.length - 1];

        for (int i = 0; i < message.length; i++) {
            bytes[i] = message[i];
        }

        this.history.add(bytes);
        this.onMessage(message);
    }

    public abstract void onMessage(byte... message);

    public List<Byte[]> getHistory() {
        return this.history;
    }
}
