package net.tridentsdk.server;

import net.tridentsdk.server.config.YamlConfiguration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Loads all the config data on start-up
 */
public class TridentConfig {

    private YamlConfiguration config;
	private short port;
	
	public TridentConfig(File properties) throws FileNotFoundException {
        FileInputStream stream = new FileInputStream(properties);
        config = new YamlConfiguration(stream);
	}
	
	public short getPort() {
		return port;
	}

}
