package net.tridentsdk.server;

import net.tridentsdk.api.Server;

public class TridentServer implements Server, Runnable {
	private TridentConfig config;
	private Thread serverThread;
    
    public TridentServer(TridentConfig config) {
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
    	TridentStart.shutdown();
    }

}
