/*
 * Copyright (C) 2014 The TridentSDK Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.tridentsdk.packets.login;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LoginManager {
    private static final LoginManager instance = new LoginManager();

    private final Map<InetSocketAddress, String> loginNames = new ConcurrentHashMap<>();

    private LoginManager() {}

    public static LoginManager getInstance() {
        return LoginManager.instance;
    }

    public void initLogin(InetSocketAddress address, String name) {
        this.loginNames.put(address, name);
    }

    public String getName(InetSocketAddress address) {
        return this.loginNames.get(address);
    }

    public void finish(InetSocketAddress address) {
        this.loginNames.remove(address);
    }
}
