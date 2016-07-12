/*
 * Trident - A Multithreaded Server Alternative
 * Copyright 2016 The TridentSDK Team
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
package net.tridentsdk.server.config;


import net.tridentsdk.config.Config;
import net.tridentsdk.config.IoResponse;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

/**
 * The default implementation of a configuration file that
 * is loaded into memory.
 *
 * @author TridentSDK
 * @since 0.5-alpha
 */
public class TridentConfig extends TridentConfigSection implements Config {
    private final Path path;

    public TridentConfig(Path path) {
        this.path = path;
        this.load();
    }

    @Override
    public File asFile() {
        return null;
    }

    @Override
    public Path asPath() {
        return null;
    }

    @Override
    public File directory() {
        return null;
    }

    @Override
    public IoResponse load() {
        return null;
    }

    @Override
    public IoResponse save() throws IOException {
        return null;
    }
}
