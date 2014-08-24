package net.tridentsdk.server;

import net.tridentsdk.api.Server;
import net.tridentsdk.server.netty.protocol.Protocol4;
import net.tridentsdk.server.netty.protocol.TridentProtocol;

public class TridentServer implements Server, Runnable {
	private TridentConfig config;
	private TridentProtocol protocol;
	private Thread serverThread;
    
    protected TridentServer(TridentConfig config) {
    	serverThread = Thread.currentThread();
        this.config = config;
        
        //TODO: Get protocol version from config... or elsewhere
        protocol = new Protocol4();
        
    }

    public TridentProtocol getProtocol() {
    	return protocol;
    }
    
    public int getPort() {
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
