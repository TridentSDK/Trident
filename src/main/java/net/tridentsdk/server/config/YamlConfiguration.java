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

package net.tridentsdk.server.config;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;

/**
 * Wrapper class over the YML file format, backed by {@link org.yaml.snakeyaml.Yaml}
 *
 * @author The TridentSDK Team
 */
public class YamlConfiguration {
    private final Yaml yaml;

    /**
     * Loads the file name and parses the YAML format
     *
     * @param string the file path (?)
     */
    public YamlConfiguration(String string) {
        this.yaml = new Yaml();
        this.yaml.load(string);
    }

    /**
     * Loads the file name and parses the YAML format
     *
     * @param stream the file stream that represents the file data
     */
    public YamlConfiguration(InputStream stream) {
        this.yaml = new Yaml();
        this.yaml.load(stream);
    }
}