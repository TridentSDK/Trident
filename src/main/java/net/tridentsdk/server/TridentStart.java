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

package net.tridentsdk.server;

import com.google.common.collect.Lists;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import joptsimple.*;
import net.tridentsdk.api.Trident;
import net.tridentsdk.server.netty.TridentChannelInitializer;

import java.io.File;
import java.util.Collection;

/**
 * Server class that starts the connection listener.
 *
 * @author The TridentSDK Team
 */
public class TridentStart {
    private static TridentStart instance;

    private EventLoopGroup bossGroup   = new NioEventLoopGroup();
    private EventLoopGroup workerGroup = new NioEventLoopGroup();

    private TridentServer server;

    /**
     * Creates a new instance of the server starter
     */
    public TridentStart() {
        TridentStart.instance = this;
    }

    /**
     * Initializes the server with the configuration file
     *
     * @param config the configuration to use for option lookup
     */
    public void init(TridentConfig config) {
        this.bossGroup = new NioEventLoopGroup();
        this.workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(this.bossGroup, this.workerGroup)
             .channel(NioServerSocketChannel.class)
             .childHandler(new TridentChannelInitializer())
             .option(ChannelOption.TCP_NODELAY, true);

            // Bind and start to accept incoming connections.
            ChannelFuture f = b.bind((int) config.getPort()).sync();

            //Runs the server on a separate thread
            //Server should read all settings from the loaded config
            this.server = new TridentServer(config);
            Trident.setServer(this.server);
            new Thread(this.server).start();

            // Wait until the server socket is closed, to gracefully shut down your server.
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            //Exception is caught if server is closed.
        } finally {
            this.close();
        }
    }

    /**
     * Shuts down the server by closing the backed event loops
     */
    public void close() {
        //Correct way to close the socket and shut down the server
        this.workerGroup.shutdownGracefully().awaitUninterruptibly();
        this.bossGroup.shutdownGracefully().awaitUninterruptibly();
    }

    /**
     * Shutdown hook
     */
    protected static void shutdown() {
        TridentStart.instance.close();
    }

    /**
     * Starts the server up when the jarfile is run
     *
     * @param args the command line arguments
     */
    public static void main(String... args) {
        /*TODO:
         check some args here, using an interpreter
         parse the configuration file
         create the server from the args/config values
         */

        OptionParser parser = new OptionParser();
        parser.acceptsAll(TridentStart.asList("h", "help"), "Show this help dialog.").forHelp();
        OptionSpec<Boolean> append =
                parser.acceptsAll(TridentStart.asList("log-append"), "Whether to append to the log file")
                      .withRequiredArg()
                      .ofType(Boolean.class)
                      .defaultsTo(true)
                      .describedAs("Log append");
        OptionSpec<File> properties =
                parser.acceptsAll(TridentStart.asList("properties"), "The location for the properties file")
                      .withRequiredArg()
                      .ofType(File.class)
                      .defaultsTo(new File("server.yml"))
                      .describedAs("Properties file");

        OptionSet options;
        try {
            options = parser.parse(args);
        } catch (OptionException ex) {
            ex.printStackTrace();
            return;
        }

        new TridentStart().init(new TridentConfig(options.valueOf(properties)));
    }

    private static Collection<String> asList(String... params) {
        return Lists.newArrayList(params);
    }
}
