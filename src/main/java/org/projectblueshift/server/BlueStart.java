package org.projectblueshift.server;

import org.projectblueshift.server.netty.BlueChannelInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class BlueStart {
	static BlueStart INSTANCE;
	static int DEFAULT_PORT = 65536;
	
	EventLoopGroup bossGroup = new NioEventLoopGroup();
    EventLoopGroup workerGroup = new NioEventLoopGroup();
	
	BlueServer server;

	public BlueStart () {
		INSTANCE = this;
	}
	
	public void init(BlueConfig config) {
		bossGroup = new NioEventLoopGroup();
	    workerGroup = new NioEventLoopGroup();
		
		try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class)
             .childHandler(new BlueChannelInitializer())
             .option(ChannelOption.TCP_NODELAY, true);

            // Bind and start to accept incoming connections.
            ChannelFuture f = b.bind(config.getPort()).sync();
            
            //Runs the server on a seperate thread
            //Server should read all settings from the loaded config
            server = new BlueServer(config);
            new Thread(server).run();
            
            // Wait until the server socket is closed, to gracefully shut down your server.
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
        	//Exception is caught if server is closed.
        	
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			close();
        }
	}
	
	public void close() {
		//Correct way to close the socket and shut down the server
		workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
	}
	
	protected static void shutdown() {
		INSTANCE.close();
	}
	
    public static void main(String[] args) {
        /*TODO:
         check some args here, using an interpreter
         parse the configuration file
         create the server from the args/config values
         */
    	
    	new BlueStart().init(new BlueConfig(DEFAULT_PORT));
    }

}
