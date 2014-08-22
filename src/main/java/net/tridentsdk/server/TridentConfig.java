package org.projectblueshift.server;

import org.projectblueshift.server.config.YamlConfiguration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Loads all the config data on start-up
 */
public class BlueConfig {

    private YamlConfiguration config;
	private short port;
	
	public BlueConfig(File properties) throws FileNotFoundException {
        FileInputStream stream = new FileInputStream(properties);
        config = new YamlConfiguration(stream);
	}
	
	public short getPort() {
		return port;
	}

}
