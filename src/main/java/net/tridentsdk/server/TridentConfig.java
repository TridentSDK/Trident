/*
 * Copyright (c) 2014, The TridentSDK Team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     1. Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *     2. Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *     3. Neither the name of the The TridentSDK Team nor the
 *        names of its contributors may be used to endorse or promote products
 *        derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL The TridentSDK Team BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.tridentsdk.server;

import net.tridentsdk.server.config.YamlConfiguration;

import javax.annotation.concurrent.ThreadSafe;
import java.io.File;

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
