package net.tridentsdk.server;

/**
 * Loads all the config data on start-up
 */
public class TridentConfig {
	private int port;
	
	public TridentConfig(int port) {
		//TODO: Load all settings from file
	}
	
	public int getPort() {
		return port;
	}
}
