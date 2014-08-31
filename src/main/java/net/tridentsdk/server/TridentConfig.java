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

package net.tridentsdk.server;

import net.tridentsdk.server.config.YamlConfiguration;

import javax.annotation.concurrent.ThreadSafe;
import java.io.File;
import java.io.FileNotFoundException;

/**
 * The configuration holder that wraps the server's configuration defaults and values upon startup
 *
 * @author The TridentSDK Team
 */
@ThreadSafe
public class TridentConfig {
    private static final int DEFAULT_PORT = 25565;
    private final short port;
    private final YamlConfiguration config;

    /**
     * Wraps the properties file and converts it to the configuration format usable by the server
     *
     * @param properties the properties file specifying options for the server use
     */
    public TridentConfig(File properties) {
        /*FileInputStream stream = new FileInputStream(properties); */
        this.config = null; /* new YamlConfiguration(stream) */

        // TODO: Temporary
        this.port = (short) TridentConfig.DEFAULT_PORT;
    }

    /**
     * Get the cached port from the configuration
     *
     * @return the port that is used by the server as specified in the config
     */
    public short getPort() {
        return this.port;
    }
}
