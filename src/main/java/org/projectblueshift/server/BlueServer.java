package org.projectblueshift.server;

import org.projectblueshift.api.Server;

public class BlueServer implements Server, Runnable {
	private BlueConfig config;
	private Thread serverThread;
    
    public BlueServer(BlueConfig config) {
    	serverThread = Thread.currentThread();
        this.config = config;
        
    }

    public short getPort() {
        return config.getPort();
    }

    @Override
	public void run() {
    	//TODO: Set some server stuff up
		//TODO: Main server Loop
	}

    public void shutdown() {
    	//TODO: Cleanup stuff...
    	BlueStart.shutdown();
    }

}
