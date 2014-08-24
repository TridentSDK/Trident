package net.tridentsdk.server;

import net.tridentsdk.server.config.YamlConfiguration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Loads all the config data on start-up
 */
public class TridentConfig {
	private static short DEFAULT_PORT = 25565;
	
    private YamlConfiguration config;
	private short port;
	
	public TridentConfig(File properties) throws FileNotFoundException {
        /*FileInputStream stream = new FileInputStream(properties);
        config = new YamlConfiguration(stream);*/
        
        //TODO: Temporary
        port = DEFAULT_PORT;
	}
	
	public short getPort() {
		return port;
	}

}
